import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { Documento } from '../../core/models/app.models';
import { DocumentPage, DocumentsService } from '../services/documents.service';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css']
})
export class DocumentsComponent implements OnInit {
  vistaActiva: 'carga' | 'visualizacion' = 'visualizacion';
  documentos: Documento[] = [];
  clasificaciones: string[] = [];
  terminoBusqueda = '';
  clasificacionSeleccionada = '';
  cargando = false;
  error: string | null = null;
  subiendoArchivo = false;

  archivoSeleccionado: File | null = null;
  formularioCarga = {
    titulo: '',
    clasificacion: 'INTERNO',
    descripcion: '',
    departamento: '',
    etiquetas: '',
    version: '1.0',
    autorNombre: ''
  };

  pagina = 0;
  readonly tamanoPagina = 10;
  totalElementos = 0;
  totalPaginas = 0;

  constructor(
    private documentsService: DocumentsService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const vista = params.get('vista');
      this.vistaActiva = vista === 'carga' ? 'carga' : 'visualizacion';

      if (this.mostrarVisualizacion) {
        this.cargarDocumentos();
      }
    });
  }

  get mostrarCarga(): boolean {
    return this.vistaActiva === 'carga';
  }

  get mostrarVisualizacion(): boolean {
    return this.vistaActiva === 'visualizacion';
  }

  irAVista(vista: 'carga' | 'visualizacion'): void {
    if (this.vistaActiva === vista) {
      return;
    }
    this.router.navigate(['/app/documentos', vista]);
  }

  get paginaActual(): number {
    return this.pagina + 1;
  }

  get puedeRetroceder(): boolean {
    return this.pagina > 0;
  }

  get puedeAvanzar(): boolean {
    return this.pagina + 1 < this.totalPaginas;
  }

  cargarDocumentos(): void {
    this.cargando = true;
    this.error = null;

    const request$ = this.terminoBusqueda.trim().length > 0
      ? this.documentsService.buscarDocumentos(this.terminoBusqueda.trim(), this.pagina, this.tamanoPagina)
      : this.documentsService.obtenerDocumentosActivos(this.pagina, this.tamanoPagina);

    request$
      .pipe(finalize(() => {
        this.cargando = false;
      }))
      .subscribe({
        next: (page: DocumentPage) => {
          const documentos = this.filtrarPorClasificacion(page.content);
          this.documentos = documentos;
          this.totalElementos = page.totalElements;
          this.totalPaginas = page.totalPages;
          this.clasificaciones = this.construirClasificaciones(page.content);
        },
        error: () => {
          this.documentos = [];
          this.error = 'No se pudieron cargar los documentos en este momento.';
        }
      });
  }

  buscar(): void {
    this.pagina = 0;
    this.cargarDocumentos();
  }

  refrescar(): void {
    this.cargarDocumentos();
    this.snackBar.open('Listado actualizado.', 'Cerrar', { duration: 2000 });
  }

  limpiarFiltros(): void {
    this.terminoBusqueda = '';
    this.clasificacionSeleccionada = '';
    this.pagina = 0;
    this.cargarDocumentos();
  }

  cambiarClasificacion(): void {
    this.pagina = 0;
    this.cargarDocumentos();
  }

  paginaAnterior(): void {
    if (!this.puedeRetroceder) {
      return;
    }
    this.pagina -= 1;
    this.cargarDocumentos();
  }

  paginaSiguiente(): void {
    if (!this.puedeAvanzar) {
      return;
    }
    this.pagina += 1;
    this.cargarDocumentos();
  }

  descargarDocumento(documento: Documento): void {
    if (!documento.activo || !documento.rutaArchivo) {
      this.snackBar.open('El documento no tiene un archivo disponible para abrir.', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    this.documentsService.descargarDocumento(documento.id).subscribe({
      next: (response) => {
        const blob = response.body;
        if (!blob) {
          this.snackBar.open('No se pudo descargar el documento.', 'Cerrar', {
            duration: 3000
          });
          return;
        }

        const nombre = this.extraerNombreDescarga(response) || this.generarNombreDescarga(documento);
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = nombre;
        anchor.click();
        window.URL.revokeObjectURL(url);

        documento.numeroDescargas = (documento.numeroDescargas ?? 0) + 1;
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404 && documento.rutaArchivo) {
          this.descargarDesdeRutaDirecta(documento);
          return;
        }

        this.snackBar.open('No se pudo descargar el documento. Intente nuevamente.', 'Cerrar', {
          duration: 3000
        });
      }
    });
  }

  seleccionarArchivo(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length > 0 ? input.files[0] : null;
    this.archivoSeleccionado = file;
  }

  cargarDocumento(): void {
    if (this.subiendoArchivo) {
      return;
    }

    if (!this.archivoSeleccionado) {
      this.snackBar.open('Debe seleccionar un archivo para cargar.', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    if (!this.formularioCarga.titulo.trim()) {
      this.snackBar.open('El titulo es obligatorio.', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    if (!this.formularioCarga.clasificacion.trim()) {
      this.snackBar.open('La clasificacion es obligatoria.', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    this.subiendoArchivo = true;
    this.documentsService
      .subirDocumento({
        archivo: this.archivoSeleccionado,
        titulo: this.formularioCarga.titulo.trim(),
        clasificacion: this.formularioCarga.clasificacion.trim(),
        descripcion: this.formularioCarga.descripcion,
        departamento: this.formularioCarga.departamento,
        etiquetas: this.formularioCarga.etiquetas,
        version: this.formularioCarga.version,
        autorNombre: this.formularioCarga.autorNombre
      })
      .pipe(finalize(() => {
        this.subiendoArchivo = false;
      }))
      .subscribe({
        next: () => {
          this.snackBar.open('Documento cargado correctamente.', 'Cerrar', {
            duration: 2500
          });
          this.reiniciarFormularioCarga();
          this.pagina = 0;
          this.cargarDocumentos();
        },
        error: () => {
          this.snackBar.open('No se pudo cargar el documento. Verifique permisos o formato.', 'Cerrar', {
            duration: 3500
          });
        }
      });
  }

  private reiniciarFormularioCarga(): void {
    this.archivoSeleccionado = null;
    this.formularioCarga = {
      titulo: '',
      clasificacion: 'INTERNO',
      descripcion: '',
      departamento: '',
      etiquetas: '',
      version: '1.0',
      autorNombre: ''
    };
  }

  obtenerUrlArchivo(rutaArchivo?: string): string {
    if (!rutaArchivo) {
      return '#';
    }

    if (/^https?:\/\//i.test(rutaArchivo)) {
      return rutaArchivo;
    }

    const backendBaseUrl = this.documentsService.obtenerBackendBaseUrl();
    return `${backendBaseUrl}${rutaArchivo.startsWith('/') ? '' : '/'}${rutaArchivo}`;
  }

  obtenerIcono(tipo: string): string {
    const valor = tipo.toLowerCase();
    if (valor.includes('pdf')) {
      return 'picture_as_pdf';
    }
    if (valor.includes('xls') || valor.includes('excel')) {
      return 'table_chart';
    }
    if (valor.includes('doc')) {
      return 'description';
    }
    return 'insert_drive_file';
  }

  trackByDocumento(index: number, documento: Documento): number {
    return documento.id ?? index;
  }

  private extraerNombreDescarga(response: HttpResponse<Blob>): string | null {
    const contentDisposition = response.headers.get('content-disposition');
    if (!contentDisposition) {
      return null;
    }

    const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
    if (utf8Match && utf8Match[1]) {
      return decodeURIComponent(utf8Match[1]);
    }

    const simpleMatch = contentDisposition.match(/filename="?([^";]+)"?/i);
    if (simpleMatch && simpleMatch[1]) {
      return simpleMatch[1];
    }

    return null;
  }

  private generarNombreDescarga(documento: Documento): string {
    const tituloBase = (documento.titulo || 'documento').trim().replace(/[\\/:*?"<>|]/g, '_');
    const ext = (documento.tipo || '').toLowerCase().replace(/[^a-z0-9]/g, '');

    if (!ext) {
      return tituloBase;
    }
    return `${tituloBase}.${ext}`;
  }

  private descargarDesdeRutaDirecta(documento: Documento): void {
    if (!documento.rutaArchivo) {
      return;
    }

    this.documentsService.descargarDocumentoDesdeRuta(documento.rutaArchivo).subscribe({
      next: (blob) => {
        const nombre = this.generarNombreDescarga(documento);
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = nombre;
        anchor.click();
        window.URL.revokeObjectURL(url);

        this.documentsService.registrarDescarga(documento.id).subscribe({
          next: () => {
            documento.numeroDescargas = (documento.numeroDescargas ?? 0) + 1;
          }
        });
      },
      error: () => {
        this.snackBar.open('No se pudo descargar el documento. Intente nuevamente.', 'Cerrar', {
          duration: 3000
        });
      }
    });
  }

  private filtrarPorClasificacion(documentos: Documento[]): Documento[] {
    if (!this.clasificacionSeleccionada) {
      return documentos;
    }

    const filtro = this.clasificacionSeleccionada.toLowerCase();
    return documentos.filter((documento) => documento.clasificacion?.toLowerCase() === filtro);
  }

  private construirClasificaciones(documentos: Documento[]): string[] {
    const valores = documentos
      .map((documento) => documento.clasificacion)
      .filter((valor): valor is string => !!valor && valor.trim().length > 0)
      .map((valor) => valor.trim());

    return Array.from(new Set(valores)).sort((a, b) => a.localeCompare(b));
  }
}

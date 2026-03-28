import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';
import { Documento } from '../../core/models/app.models';
import { DocumentPage, DocumentsService } from '../services/documents.service';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css']
})
export class DocumentsComponent implements OnInit {
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
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarDocumentos();
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

  registrarDescarga(documento: Documento, event?: MouseEvent): void {
    if (!documento.activo || !documento.rutaArchivo) {
      event?.preventDefault();
      this.snackBar.open('El documento no tiene un archivo disponible para abrir.', 'Cerrar', {
        duration: 3000
      });
      return;
    }

    this.documentsService.registrarDescarga(documento.id).subscribe({
      next: () => {
        documento.numeroDescargas = (documento.numeroDescargas ?? 0) + 1;
      },
      error: () => {
        this.snackBar.open('No se pudo registrar la descarga. Intente nuevamente.', 'Cerrar', {
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

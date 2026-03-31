import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';
import { Documento } from '../../core/models/app.models';
import { DocumentPage, DocumentsService } from '../services/documents.service';

type SortMode = 'popular' | 'recent' | 'alpha';
type ViewMode = 'grid' | 'list';

interface CategorySummary {
  nombre: string;
  cantidad: number;
  totalDescargas: number;
  ultimaActualizacion: Date | null;
}

interface AreaSummary {
  nombre: string;
  documentos: Documento[];
  totalDescargas: number;
  ultimaActualizacion: Date | null;
}

@Component({
  selector: 'app-documents-areas-categories',
  templateUrl: './documents-areas-categories.component.html',
  styleUrls: ['./documents-areas-categories.component.css']
})
export class DocumentsAreasCategoriesComponent implements OnInit {
  cargando = false;
  error: string | null = null;

  terminoBusqueda = '';
  modoVista: ViewMode = 'grid';
  modoOrden: SortMode = 'popular';

  documentos: Documento[] = [];
  documentosListado: Documento[] = [];
  categorias: CategorySummary[] = [];
  areas: AreaSummary[] = [];
  categoriaActiva: string | null = null;

  areaActiva: AreaSummary | null = null;
  paginaActual = 1;
  readonly tamanoPagina = 10;
  totalPaginas = 1;

  constructor(
    private documentsService: DocumentsService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarDocumentos();
  }

  get documentosPagina(): Documento[] {
    const inicio = (this.paginaActual - 1) * this.tamanoPagina;
    return this.documentosListado.slice(inicio, inicio + this.tamanoPagina);
  }

  get totalDocumentosListado(): number {
    return this.documentosListado.length;
  }

  cargarDocumentos(): void {
    this.cargando = true;
    this.error = null;
    this.cargarPagina(0, 100, []);
  }

  aplicarBusqueda(): void {
    this.recalcularVista();
  }

  cambiarOrden(): void {
    this.categorias = this.ordenarCategorias(this.categorias);
  }

  seleccionarCategoria(categoria: CategorySummary): void {
    this.categoriaActiva = this.categoriaActiva === categoria.nombre ? null : categoria.nombre;
    this.paginaActual = 1;
    this.recalcularVista();
  }

  limpiarFiltroCategoria(): void {
    if (!this.categoriaActiva) {
      return;
    }
    this.categoriaActiva = null;
    this.paginaActual = 1;
    this.recalcularVista();
  }

  esCategoriaActiva(categoria: CategorySummary): boolean {
    return this.categoriaActiva === categoria.nombre;
  }

  seleccionarArea(area: AreaSummary): void {
    this.areaActiva = this.areaActiva?.nombre === area.nombre ? null : area;
    this.paginaActual = 1;
    this.recalcularVista();
  }

  limpiarFiltroArea(): void {
    if (!this.areaActiva) {
      return;
    }
    this.areaActiva = null;
    this.paginaActual = 1;
    this.recalcularVista();
  }

  esAreaActiva(area: AreaSummary): boolean {
    return this.areaActiva?.nombre === area.nombre;
  }

  paginaAnterior(): void {
    if (this.paginaActual > 1) {
      this.paginaActual -= 1;
    }
  }

  paginaSiguiente(): void {
    if (this.paginaActual < this.totalPaginas) {
      this.paginaActual += 1;
    }
  }

  descargarDocumento(documento: Documento): void {
    if (!documento.activo || !documento.rutaArchivo) {
      this.snackBar.open('El documento no esta disponible para descarga.', 'Cerrar', {
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
        this.dispararDescarga(blob, nombre);
        documento.numeroDescargas = (documento.numeroDescargas ?? 0) + 1;
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404 && documento.rutaArchivo) {
          this.documentsService.descargarDocumentoDesdeRuta(documento.rutaArchivo).subscribe({
            next: (blob) => {
              this.dispararDescarga(blob, this.generarNombreDescarga(documento));
              this.documentsService.registrarDescarga(documento.id).subscribe();
              documento.numeroDescargas = (documento.numeroDescargas ?? 0) + 1;
            },
            error: () => {
              this.snackBar.open('No se pudo descargar el documento.', 'Cerrar', {
                duration: 3000
              });
            }
          });
          return;
        }

        this.snackBar.open('No se pudo descargar el documento.', 'Cerrar', {
          duration: 3000
        });
      }
    });
  }

  obtenerIconoCategoria(nombre: string): string {
    const valor = nombre.toLowerCase();
    if (valor.includes('norma')) {
      return 'gavel';
    }
    if (valor.includes('proced')) {
      return 'account_tree';
    }
    if (valor.includes('manual')) {
      return 'menu_book';
    }
    if (valor.includes('audit') || valor.includes('auditor')) {
      return 'fact_check';
    }
    if (valor.includes('acta') || valor.includes('minuta')) {
      return 'groups';
    }
    return 'folder';
  }

  obtenerIconoArea(nombre: string): string {
    const valor = nombre.toLowerCase();
    if (valor.includes('human') || valor.includes('rrhh')) {
      return 'groups';
    }
    if (valor.includes('riesgo')) {
      return 'security';
    }
    if (valor.includes('tec') || valor.includes('it') || valor.includes('ti')) {
      return 'settings_suggest';
    }
    if (valor.includes('legal')) {
      return 'account_balance';
    }
    if (valor.includes('opera')) {
      return 'payments';
    }
    return 'business';
  }

  claseChip(documento: Documento): string {
    const valor = (documento.clasificacion || '').toLowerCase();
    if (valor.includes('conf')) {
      return 'chip-conf';
    }
    if (valor.includes('res')) {
      return 'chip-res';
    }
    return 'chip-public';
  }

  private cargarPagina(page: number, size: number, acumulado: Documento[]): void {
    this.documentsService
      .obtenerDocumentosActivos(page, size)
      .pipe(finalize(() => {
        if (page === 0) {
          this.cargando = false;
        }
      }))
      .subscribe({
        next: (result: DocumentPage) => {
          const siguiente = acumulado.concat(result.content || []);

          if (page + 1 < result.totalPages) {
            this.cargarPagina(page + 1, size, siguiente);
            return;
          }

          this.documentos = siguiente;
          this.recalcularVista();
          this.cargando = false;
        },
        error: () => {
          this.error = 'No se pudieron cargar los documentos por areas y categorias.';
          this.cargando = false;
        }
      });
  }

  private recalcularVista(): void {
    const docsFiltradosBusqueda = this.filtrarDocumentos(this.documentos);
    this.categorias = this.ordenarCategorias(this.construirCategorias(docsFiltradosBusqueda));

    if (this.categoriaActiva) {
      const categoriaDisponible = this.categorias.some((categoria) => categoria.nombre === this.categoriaActiva);
      if (!categoriaDisponible) {
        this.categoriaActiva = null;
      }
    }

    const docsFiltrados = this.categoriaActiva
      ? docsFiltradosBusqueda.filter((doc) => (doc.clasificacion || 'Sin categoria').trim() === this.categoriaActiva)
      : docsFiltradosBusqueda;

    this.areas = this.construirAreas(docsFiltrados);

    if (this.areaActiva) {
      const areaDisponible = this.areas.some((area) => area.nombre === this.areaActiva?.nombre);
      if (!areaDisponible) {
        this.areaActiva = null;
      }
    }

    const docsArea = this.areaActiva
      ? docsFiltrados.filter((doc) => (doc.departamento || 'General').trim() === this.areaActiva?.nombre)
      : docsFiltrados;

    this.documentosListado = [...docsArea].sort((a, b) => {
      const fa = a.fechaCreacion ? new Date(a.fechaCreacion).getTime() : 0;
      const fb = b.fechaCreacion ? new Date(b.fechaCreacion).getTime() : 0;
      return fb - fa;
    });

    this.actualizarPaginacion();

    if (!this.areaActiva) {
      return;
    }

    const actualizada = this.areas.find((area) => area.nombre === this.areaActiva?.nombre) || null;
    this.areaActiva = actualizada;
  }

  private actualizarPaginacion(): void {
    this.totalPaginas = Math.max(1, Math.ceil(this.documentosListado.length / this.tamanoPagina));
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = this.totalPaginas;
    }
    if (this.paginaActual < 1) {
      this.paginaActual = 1;
    }
  }

  private filtrarDocumentos(documentos: Documento[]): Documento[] {
    const termino = this.terminoBusqueda.trim().toLowerCase();
    if (!termino) {
      return documentos;
    }

    return documentos.filter((doc) => {
      return [doc.titulo, doc.descripcion, doc.clasificacion, doc.departamento, doc.etiquetas, doc.tipo]
        .filter((v): v is string => !!v)
        .some((v) => v.toLowerCase().includes(termino));
    });
  }

  private construirCategorias(documentos: Documento[]): CategorySummary[] {
    const mapa = new Map<string, CategorySummary>();

    documentos.forEach((doc) => {
      const key = (doc.clasificacion || 'Sin categoria').trim();
      const actual = mapa.get(key) || {
        nombre: key,
        cantidad: 0,
        totalDescargas: 0,
        ultimaActualizacion: null
      };

      actual.cantidad += 1;
      actual.totalDescargas += doc.numeroDescargas || 0;

      const fecha = doc.fechaCreacion ? new Date(doc.fechaCreacion) : null;
      if (fecha && (!actual.ultimaActualizacion || fecha > actual.ultimaActualizacion)) {
        actual.ultimaActualizacion = fecha;
      }

      mapa.set(key, actual);
    });

    return Array.from(mapa.values());
  }

  private ordenarCategorias(categorias: CategorySummary[]): CategorySummary[] {
    const copia = [...categorias];

    if (this.modoOrden === 'alpha') {
      return copia.sort((a, b) => a.nombre.localeCompare(b.nombre));
    }

    if (this.modoOrden === 'recent') {
      return copia.sort((a, b) => {
        const fa = a.ultimaActualizacion ? a.ultimaActualizacion.getTime() : 0;
        const fb = b.ultimaActualizacion ? b.ultimaActualizacion.getTime() : 0;
        return fb - fa;
      });
    }

    return copia.sort((a, b) => b.totalDescargas - a.totalDescargas);
  }

  private construirAreas(documentos: Documento[]): AreaSummary[] {
    const mapa = new Map<string, AreaSummary>();

    documentos.forEach((doc) => {
      const key = (doc.departamento || 'General').trim();
      const actual = mapa.get(key) || {
        nombre: key,
        documentos: [],
        totalDescargas: 0,
        ultimaActualizacion: null
      };

      actual.documentos.push(doc);
      actual.totalDescargas += doc.numeroDescargas || 0;

      const fecha = doc.fechaCreacion ? new Date(doc.fechaCreacion) : null;
      if (fecha && (!actual.ultimaActualizacion || fecha > actual.ultimaActualizacion)) {
        actual.ultimaActualizacion = fecha;
      }

      mapa.set(key, actual);
    });

    return Array.from(mapa.values())
      .map((area) => ({
        ...area,
        documentos: [...area.documentos].sort((a, b) => {
          const fa = a.fechaCreacion ? new Date(a.fechaCreacion).getTime() : 0;
          const fb = b.fechaCreacion ? new Date(b.fechaCreacion).getTime() : 0;
          return fb - fa;
        })
      }))
      .sort((a, b) => {
        if (b.documentos.length === a.documentos.length) {
          return a.nombre.localeCompare(b.nombre);
        }
        return b.documentos.length - a.documentos.length;
      });
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

  private dispararDescarga(blob: Blob, nombre: string): void {
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = nombre;
    anchor.click();
    window.URL.revokeObjectURL(url);
  }
}

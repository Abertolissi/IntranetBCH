import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Documento } from '../../core/models/app.models';
import { ApiResponse } from '../../core/models/auth.models';

export interface DocumentPage {
  content: Documento[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentsService {
  private readonly apiUrl = `${environment.apiUrl}/documentos`;

  constructor(private http: HttpClient) {}

  obtenerDocumentosActivos(page: number, size: number): Observable<DocumentPage> {
    const params = this.crearParams(page, size);
    return this.http
      .get<ApiResponse<DocumentPage>>(this.apiUrl, { params })
      .pipe(map((response) => response.data));
  }

  buscarDocumentos(titulo: string, page: number, size: number): Observable<DocumentPage> {
    const params = this.crearParams(page, size).set('titulo', titulo);
    return this.http
      .get<ApiResponse<DocumentPage>>(`${this.apiUrl}/buscar`, { params })
      .pipe(map((response) => response.data));
  }

  registrarDescarga(id: number): Observable<null> {
    return this.http
      .post<ApiResponse<null>>(`${this.apiUrl}/${id}/descargar`, {})
      .pipe(map((response) => response.data));
  }

  crearDocumento(documento: Partial<Documento>): Observable<Documento> {
    return this.http
      .post<ApiResponse<Documento>>(this.apiUrl, documento)
      .pipe(map((response) => response.data));
  }

  subirDocumento(payload: {
    archivo: File;
    titulo: string;
    clasificacion: string;
    descripcion?: string;
    departamento?: string;
    etiquetas?: string;
    version?: string;
    autorNombre?: string;
  }): Observable<Documento> {
    const formData = new FormData();
    formData.append('archivo', payload.archivo);
    formData.append('titulo', payload.titulo);
    formData.append('clasificacion', payload.clasificacion);

    if (payload.descripcion?.trim()) {
      formData.append('descripcion', payload.descripcion.trim());
    }
    if (payload.departamento?.trim()) {
      formData.append('departamento', payload.departamento.trim());
    }
    if (payload.etiquetas?.trim()) {
      formData.append('etiquetas', payload.etiquetas.trim());
    }
    if (payload.version?.trim()) {
      formData.append('version', payload.version.trim());
    }
    if (payload.autorNombre?.trim()) {
      formData.append('autorNombre', payload.autorNombre.trim());
    }

    return this.http
      .post<ApiResponse<Documento>>(`${this.apiUrl}/upload`, formData)
      .pipe(map((response) => response.data));
  }

  obtenerBackendBaseUrl(): string {
    return environment.apiUrl.replace(/\/api\/?$/, '');
  }

  private crearParams(page: number, size: number): HttpParams {
    return new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', 'fechaCreacion,desc');
  }
}
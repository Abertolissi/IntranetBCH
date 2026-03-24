export interface Noticia {
  id: number;
  titulo: string;
  contenido: string;
  activa: boolean;
  fechaPublicacion: Date;
  fechaExpiracion?: Date;
  autorNombre: string;
  numeroLecturas: number;
  prioridad: string;
  categoria: string;
  imagenUrl?: string;
  fechaCreacion: Date;
  fechaActualizacion: Date;
}

export interface Documento {
  id: number;
  titulo: string;
  descripcion: string;
  tipo: string;
  clasificacion: string;
  activo: boolean;
  numeroDescargas: number;
  autorNombre: string;
  departamento: string;
  etiquetas: string;
  version: string;
  fechaVigenciaInicio?: Date;
  fechaVigenciaFin?: Date;
  fechaCreacion: Date;
  rutaArchivo: string;
  tamano: number;
}

export interface DashboardData {
  totalUsuarios: number;
  usuariosActivos: number;
  noticiasRecientes: number;
  documentosDisponibles: number;
}

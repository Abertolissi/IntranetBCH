export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  numeroEmpleado: string;
  departamento: string;
  puesto: string;
  activo: boolean;
  cuentaBloqueada: boolean;
  roles: string[];
  fechaUltimoLogin: Date;
  fechaCreacion: Date;
}

export interface LoginRequest {
  usuario: string;
  contrasena: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  noNombreUsuario: string;
  email: string;
  nombreCompleto: string;
  usuarioId: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  timestamp: Date;
  path?: string;
}

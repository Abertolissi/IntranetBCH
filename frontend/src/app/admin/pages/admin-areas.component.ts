import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CatalogManagementService } from '../../core/services/catalog-management.service';

@Component({
  selector: 'app-admin-areas',
  templateUrl: './admin-areas.component.html',
  styleUrls: ['./admin-areas.component.css']
})
export class AdminAreasComponent implements OnInit {
  areas: string[] = [];
  nuevaArea = '';
  areaEnEdicion: string | null = null;
  areaEditada = '';

  constructor(
    private catalogManagementService: CatalogManagementService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.refrescarAreas();
  }

  crearArea(): void {
    if (!this.catalogManagementService.addArea(this.nuevaArea)) {
      this.snackBar.open('No se pudo crear el area. Verifica que no exista.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.nuevaArea = '';
    this.refrescarAreas();
    this.snackBar.open('Area creada.', 'Cerrar', { duration: 2000 });
  }

  iniciarEdicion(area: string): void {
    this.areaEnEdicion = area;
    this.areaEditada = area;
  }

  cancelarEdicion(): void {
    this.areaEnEdicion = null;
    this.areaEditada = '';
  }

  guardarEdicion(): void {
    if (!this.areaEnEdicion) {
      return;
    }

    if (!this.catalogManagementService.updateArea(this.areaEnEdicion, this.areaEditada)) {
      this.snackBar.open('No se pudo actualizar el area. Verifica duplicados.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.cancelarEdicion();
    this.refrescarAreas();
    this.snackBar.open('Area actualizada.', 'Cerrar', { duration: 2000 });
  }

  eliminarArea(area: string): void {
    if (!this.catalogManagementService.removeArea(area)) {
      this.snackBar.open('No se pudo eliminar el area.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.refrescarAreas();
    this.snackBar.open('Area eliminada.', 'Cerrar', { duration: 2000 });
  }

  private refrescarAreas(): void {
    this.areas = this.catalogManagementService.getAreas();
  }
}

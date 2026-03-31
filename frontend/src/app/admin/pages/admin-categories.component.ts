import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CatalogManagementService } from '../../core/services/catalog-management.service';

@Component({
  selector: 'app-admin-categories',
  templateUrl: './admin-categories.component.html',
  styleUrls: ['./admin-categories.component.css']
})
export class AdminCategoriesComponent implements OnInit {
  categorias: string[] = [];
  nuevaCategoria = '';
  categoriaEnEdicion: string | null = null;
  categoriaEditada = '';

  constructor(
    private catalogManagementService: CatalogManagementService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.refrescarCategorias();
  }

  crearCategoria(): void {
    if (!this.catalogManagementService.addCategory(this.nuevaCategoria)) {
      this.snackBar.open('No se pudo crear la categoria. Verifica que no exista.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.nuevaCategoria = '';
    this.refrescarCategorias();
    this.snackBar.open('Categoria creada.', 'Cerrar', { duration: 2000 });
  }

  iniciarEdicion(categoria: string): void {
    this.categoriaEnEdicion = categoria;
    this.categoriaEditada = categoria;
  }

  cancelarEdicion(): void {
    this.categoriaEnEdicion = null;
    this.categoriaEditada = '';
  }

  guardarEdicion(): void {
    if (!this.categoriaEnEdicion) {
      return;
    }

    if (!this.catalogManagementService.updateCategory(this.categoriaEnEdicion, this.categoriaEditada)) {
      this.snackBar.open('No se pudo actualizar la categoria. Verifica duplicados.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.cancelarEdicion();
    this.refrescarCategorias();
    this.snackBar.open('Categoria actualizada.', 'Cerrar', { duration: 2000 });
  }

  eliminarCategoria(categoria: string): void {
    if (!this.catalogManagementService.removeCategory(categoria)) {
      this.snackBar.open('No se pudo eliminar la categoria.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.refrescarCategorias();
    this.snackBar.open('Categoria eliminada.', 'Cerrar', { duration: 2000 });
  }

  private refrescarCategorias(): void {
    this.categorias = this.catalogManagementService.getCategories();
  }
}

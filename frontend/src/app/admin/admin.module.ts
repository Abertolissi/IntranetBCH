import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminComponent } from './pages/admin.component';
import { AdminCategoriesComponent } from './pages/admin-categories.component';
import { AdminAreasComponent } from './pages/admin-areas.component';
import { LayoutModule } from '../layout/layout.module';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent
  },
  {
    path: 'categorias',
    component: AdminCategoriesComponent
  },
  {
    path: 'areas',
    component: AdminAreasComponent
  }
];

@NgModule({
  declarations: [AdminComponent, AdminCategoriesComponent, AdminAreasComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    LayoutModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ]
})
export class AdminModule { }

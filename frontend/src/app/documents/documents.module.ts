import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { DocumentsComponent } from './pages/documents.component';
import { DocumentsAreasCategoriesComponent } from './pages/documents-areas-categories.component';
import { LayoutModule } from '../layout/layout.module';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'visualizacion',
    pathMatch: 'full'
  },
  {
    path: 'areas-categorias',
    component: DocumentsAreasCategoriesComponent
  },
  {
    path: ':vista',
    component: DocumentsComponent
  }
];

@NgModule({
  declarations: [DocumentsComponent, DocumentsAreasCategoriesComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    LayoutModule
  ]
})
export class DocumentsModule { }

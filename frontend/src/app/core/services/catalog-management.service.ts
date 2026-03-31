import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CatalogManagementService {
  private readonly categoriesKey = 'intranet.catalog.categories';
  private readonly areasKey = 'intranet.catalog.areas';

  private readonly defaultCategories = [
    'Normativas',
    'Procedimientos',
    'Manuales',
    'Auditoria',
    'Minutas'
  ];

  private readonly defaultAreas = [
    'Recursos Humanos',
    'Tecnología',
    'Operaciones',
    'Riesgos',
    'Auditoria'
  ];

  getCategories(): string[] {
    return this.readList(this.categoriesKey, this.defaultCategories);
  }

  getAreas(): string[] {
    const areas = this.readList(this.areasKey, this.defaultAreas);
    const migrated = areas.map((area) => this.sameValue(area, 'Tecnologia') ? 'Tecnología' : area);
    if (JSON.stringify(areas) !== JSON.stringify(migrated)) {
      this.writeList(this.areasKey, migrated);
    }
    return migrated;
  }

  addCategory(name: string): boolean {
    return this.addItem(this.categoriesKey, this.defaultCategories, name);
  }

  addArea(name: string): boolean {
    return this.addItem(this.areasKey, this.defaultAreas, name);
  }

  updateCategory(previous: string, next: string): boolean {
    return this.updateItem(this.categoriesKey, this.defaultCategories, previous, next);
  }

  updateArea(previous: string, next: string): boolean {
    return this.updateItem(this.areasKey, this.defaultAreas, previous, next);
  }

  removeCategory(name: string): boolean {
    return this.removeItem(this.categoriesKey, this.defaultCategories, name);
  }

  removeArea(name: string): boolean {
    return this.removeItem(this.areasKey, this.defaultAreas, name);
  }

  private readList(key: string, defaults: string[]): string[] {
    const raw = localStorage.getItem(key);
    if (!raw) {
      this.writeList(key, defaults);
      return [...defaults];
    }

    try {
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) {
        this.writeList(key, defaults);
        return [...defaults];
      }

      const normalized = parsed
        .map((item) => this.normalizeValue(typeof item === 'string' ? item : ''))
        .filter((item) => item.length > 0);

      const deduplicated = Array.from(new Set(normalized));
      if (deduplicated.length === 0) {
        this.writeList(key, defaults);
        return [...defaults];
      }

      return deduplicated;
    } catch {
      this.writeList(key, defaults);
      return [...defaults];
    }
  }

  private writeList(key: string, values: string[]): void {
    localStorage.setItem(key, JSON.stringify(values));
  }

  private addItem(key: string, defaults: string[], value: string): boolean {
    const list = this.readList(key, defaults);
    const normalized = this.normalizeValue(value);
    if (!normalized) {
      return false;
    }

    if (this.containsValue(list, normalized)) {
      return false;
    }

    list.push(normalized);
    this.writeList(key, list);
    return true;
  }

  private updateItem(key: string, defaults: string[], previous: string, next: string): boolean {
    const list = this.readList(key, defaults);
    const prev = this.normalizeValue(previous);
    const nxt = this.normalizeValue(next);

    if (!prev || !nxt) {
      return false;
    }

    const previousIndex = list.findIndex((item) => this.sameValue(item, prev));
    if (previousIndex < 0) {
      return false;
    }

    if (this.containsValue(list, nxt) && !this.sameValue(prev, nxt)) {
      return false;
    }

    list[previousIndex] = nxt;
    this.writeList(key, list);
    return true;
  }

  private removeItem(key: string, defaults: string[], value: string): boolean {
    const list = this.readList(key, defaults);
    const normalized = this.normalizeValue(value);
    if (!normalized) {
      return false;
    }

    const next = list.filter((item) => !this.sameValue(item, normalized));
    if (next.length === list.length) {
      return false;
    }

    this.writeList(key, next);
    return true;
  }

  private normalizeValue(value: string): string {
    return value.replace(/\s+/g, ' ').trim();
  }

  private containsValue(values: string[], candidate: string): boolean {
    return values.some((value) => this.sameValue(value, candidate));
  }

  private sameValue(a: string, b: string): boolean {
    return a.toLocaleLowerCase() === b.toLocaleLowerCase();
  }
}

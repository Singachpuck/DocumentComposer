import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'sizeFormatter'
})
export class SizeFormatterPipe implements PipeTransform {

  transform(value: number | null, ...args: unknown[]): string {
    if (value === null || value === undefined) {
      return 'N/A';
    }

    const units = ['B', 'KB', 'MB', 'GB', 'TB'];

    let size = value;
    let unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    return size.toFixed(2) + ' ' + units[unitIndex];
  }

}

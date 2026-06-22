import { Pipe, PipeTransform } from '@angular/core';
import { LanguageService } from '../service/language.service';

@Pipe({
  name: 'translate',
  standalone: true,
  pure: false
})
export class TranslatePipe implements PipeTransform {
  constructor(private langService: LanguageService) {}

  transform(value: string): string {
    return this.langService.translate(value);
  }
}

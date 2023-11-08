import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {
  DEFAULT_ESCAPE_PLACEHOLDER,
  DEFAULT_TOKEN_PLACEHOLDER,
  TEMPLATE_SUPPORTED_FORMATS,
  UtilService
} from "../_services/util.service";
import {Template} from "../_model/template";
import {TemplateService} from "../_services/template.service";

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css']
})
export class TemplatesComponent implements OnInit {

  @ViewChild('newTemplateFile') newTemplateFile?: ElementRef;

  formats = TEMPLATE_SUPPORTED_FORMATS;

  acceptFiles?: string;

  addTemplate = new FormGroup({
    name: new FormControl(''),
    format: new FormControl(this.formats[0].name),
    beginTokenPlaceholder: new FormControl(DEFAULT_TOKEN_PLACEHOLDER.begin),
    endTokenPlaceholder: new FormControl(DEFAULT_TOKEN_PLACEHOLDER.end),
    beginEscapePlaceholder: new FormControl(DEFAULT_ESCAPE_PLACEHOLDER.begin),
    endEscapePlaceholder: new FormControl(DEFAULT_ESCAPE_PLACEHOLDER.end),
  });

  templates?: Array<Template>;

  constructor(private util: UtilService, private templateService: TemplateService) { }

  ngOnInit(): void {
    this.acceptFiles = this.formats.map(i => i.media).join(',');
    this.templateService.getTemplates().subscribe(data => {
      this.templates = data;
    })
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    this.addTemplate.get('name')?.setValue(file.name);
  }

  async onTemplateCreate() {
    let template: any = new Template();

    template.name = this.addTemplate.get('name')?.value;
    template.format = this.addTemplate.get('format')?.value;
    template.beginTokenPlaceholder = this.addTemplate.get('beginTokenPlaceholder')?.value;
    template.endTokenPlaceholder = this.addTemplate.get('endTokenPlaceholder')?.value;
    template.beginEscapePlaceholder = this.addTemplate.get('beginEscapePlaceholder')?.value;
    template.endEscapePlaceholder = this.addTemplate.get('endEscapePlaceholder')?.value;
    template.bytes = await this.util.toBase64(this.newTemplateFile?.nativeElement.files[0]);

    this.templateService.createTemplate(template).subscribe(() => {
      window.location.reload();
    });
  }
}

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {
    DATASET_SUPPORTED_FORMATS,
    DEFAULT_ESCAPE_PLACEHOLDER,
    DEFAULT_TOKEN_PLACEHOLDER,
    TEMPLATE_SUPPORTED_FORMATS,
    UtilService
} from "../_services/util.service";
import {Template} from "../_model/template";
import {TemplateService} from "../_services/template.service";
import {Dataset} from "../_model/dataset";
import {DatasetService} from "../_services/dataset.service";

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css']
})
export class TemplatesComponent implements OnInit {

  @ViewChild('newTemplateFile') newTemplateFile?: ElementRef;

  @ViewChild('newDatasetFile') newDatasetFile?: ElementRef;

  formats = TEMPLATE_SUPPORTED_FORMATS;

  datasetFormats = DATASET_SUPPORTED_FORMATS;

  acceptFiles?: string;

  acceptDatasetFiles?: string;

  addTemplate = new FormGroup({
    name: new FormControl(''),
    format: new FormControl(this.formats[0].name),
    beginTokenPlaceholder: new FormControl(DEFAULT_TOKEN_PLACEHOLDER.begin),
    endTokenPlaceholder: new FormControl(DEFAULT_TOKEN_PLACEHOLDER.end),
    beginEscapePlaceholder: new FormControl(DEFAULT_ESCAPE_PLACEHOLDER.begin),
    endEscapePlaceholder: new FormControl(DEFAULT_ESCAPE_PLACEHOLDER.end),
  });

  templates?: Array<Template>;

  addDataset = new FormGroup({
    name: new FormControl(''),
    format: new FormControl(this.datasetFormats[0].name)
  });

  datasets?: Array<Dataset>;

  constructor(private util: UtilService, private templateService: TemplateService, private datasetService: DatasetService) { }

  ngOnInit(): void {
    this.acceptFiles = this.formats.map(i => i.media).join(',');
    this.acceptDatasetFiles = this.datasetFormats.map(i => i.media).join(',');
    this.templateService.getTemplates().subscribe(data => {
      this.templates = data;
      this.templates.sort((a: Template, b: Template) => {
        // @ts-ignore
        return (new Date(b.created).getTime() - new Date(a.created).getTime());
      });
    });
    this.datasetService.getDatasets().subscribe(data => {
      this.datasets = data;
      this.datasets.sort((a: Dataset, b: Dataset) => {
        // @ts-ignore
        return (new Date(b.created).getTime() - new Date(a.created).getTime());
      });
    })
  }

  onTemplateSortBy(e: Event, type: 'name' | 'created' | 'size', order: 'asc' | 'desc') {
    e.preventDefault();

    if (type === 'name') {
      // @ts-ignore
      this.templates?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return a.name?.localeCompare(b.name) * ('asc' === order ? 1 : -1);
      });
    } else if (type === 'created') {
      this.templates?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return (new Date(a.created).getTime() - new Date(b.created).getTime()) * ('asc' === order ? 1 : -1);
      });
    } else if (type === 'size') {
      this.templates?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return (a.size - b.size) * ('asc' === order ? 1 : -1);
      });
    }
  }

  onTemplateFileSelected(event: any) {
    const file: File = event.target.files[0];
    this.addTemplate.get('name')?.setValue(file.name);
  }

  onDatasetFileSelected(event: any) {
    const file: File = event.target.files[0];
    this.addDataset.get('name')?.setValue(file.name);
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

  async onDatasetCreate() {
    let dataset: any = new Dataset();

    dataset.name = this.addDataset.get('name')?.value;
    dataset.format = this.addDataset.get('format')?.value;
    dataset.bytes = await this.util.toBase64(this.newDatasetFile?.nativeElement.files[0]);

    this.datasetService.createDataset(dataset).subscribe(() => {
      window.location.reload();
    });
  }

  onTemplateDownload() {

  }

  lookupFormat(format: string | null, type: 'dataset' | 'template') {
    for (let formatObj of type === 'template' ? this.formats : this.datasetFormats) {
      if (formatObj.name === format) {
        return formatObj;
      }
    }
    return null;
  }
}

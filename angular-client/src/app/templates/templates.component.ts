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
import {DownloadService} from "../_services/download.service";
import {ComposeService} from "../_services/compose.service";
import {ComposedDocument} from "../_model/composed-document";
import {NotificationService} from "../_services/notification.service";

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

  templates: Array<Template> = [];

  addDataset = new FormGroup({
    name: new FormControl(''),
    format: new FormControl(this.datasetFormats[0].name)
  });

  datasets: Array<Dataset> = [];

  selectedTemplate: any = null;

  selectedDataset: any = null;

  lastComposed: ComposedDocument | null = null;

  constructor(private util: UtilService, private templateService: TemplateService,
              private datasetService: DatasetService, private downloadService: DownloadService,
              private composeService: ComposeService, private notification: NotificationService) { }

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
    this.util.sortEntities(this.templates, type, order);
  }

  onDatasetSortBy(e: Event, type: 'name' | 'created' | 'size', order: 'asc' | 'desc') {
    e.preventDefault();
    this.util.sortEntities(this.datasets, type, order);
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

    this.templateService.createTemplate(template).subscribe({
      next: () => {
        window.location.reload();
      },
      error: this.notification.defaultErrorHandler
    });
  }

  async onDatasetCreate() {
    let dataset: any = new Dataset();

    dataset.name = this.addDataset.get('name')?.value;
    dataset.format = this.addDataset.get('format')?.value;
    dataset.bytes = await this.util.toBase64(this.newDatasetFile?.nativeElement.files[0]);

    this.datasetService.createDataset(dataset).subscribe({
      next: () => {
        window.location.reload();
      },
      error: this.notification.defaultErrorHandler
    });
  }

  onTemplateSelected(e: Event) {
    let target: any = e.currentTarget;
    let id = target.dataset.templateId;
    let selectedId = this.selectedTemplate && this.selectedTemplate.dataset.templateId;
    if (id === selectedId) {
      this.selectedTemplate = null;
      target.classList.remove('selected');
    } else {
      if (this.selectedTemplate !== null) {
        this.selectedTemplate.classList.remove('selected');
      }
      target.classList.add('selected');
      this.selectedTemplate = target;
    }
  }
  onDatasetSelected(e: Event) {
    let target: any = e.currentTarget;
    let id = target.dataset.datasetId;
    let selectedId = this.selectedDataset && this.selectedDataset.dataset.datasetId;
    if (id === selectedId) {
      this.selectedDataset = null;
      target.classList.remove('selected');
    } else {
      if (this.selectedDataset !== null) {
        this.selectedDataset.classList.remove('selected');
      }
      target.classList.add('selected');
      this.selectedDataset = target;
    }
  }

  onTemplateDownload(e: Event, id: any, name: any) {
    e.preventDefault();
    e.stopPropagation();
    this.downloadService.downloadTemplate(id).subscribe({
      next: response => {
        if (response.ok && response.body) {
          this.util.triggerDownload(response.body, name);
        }
      },
      error: this.notification.defaultErrorHandler
    });
  }

  onDatasetDownload(e: Event, id: any, name: any) {
    e.preventDefault();
    e.stopPropagation();
    this.downloadService.downloadDataset(id).subscribe({
      next: response => {
        if (response.ok && response.body) {
          this.util.triggerDownload(response.body, name);
        }
      },
      error: this.notification.defaultErrorHandler
    })
  }

  onComposeDownload(e: Event) {
    e.preventDefault();

    if (this.lastComposed !== null) {
      let id: any = this.lastComposed.id;
      let name: any = this.lastComposed.name;
      this.downloadService.downloadComposed(id).subscribe({
        next: response => {
          if (response.ok && response.body) {
            this.util.triggerDownload(response.body, name);
          }
        },
        error: this.notification.defaultErrorHandler
      })
    }
  }

  lookupFormat(format: string | null, type: 'dataset' | 'template') {
    for (let formatObj of type === 'template' ? this.formats : this.datasetFormats) {
      if (formatObj.name === format) {
        return formatObj;
      }
    }
    return null;
  }

  onCompose() {
    if (this.selectedTemplate !== null && this.selectedDataset !== null) {
      let templateId = this.selectedTemplate.dataset.templateId;
      let datasetId = this.selectedDataset.dataset.datasetId;
      this.composeService.composeDocument(templateId, datasetId).subscribe({
        next: doc => {
          this.lastComposed = doc;
        },
        error: this.notification.defaultErrorHandler
      });
    }
  }

  onTemplateDelete(id: any) {
    this.templateService.deleteTemplate(id).subscribe({
      next: () => {
        window.location.reload();
      },
      error: this.notification.defaultErrorHandler
    });
  }

  onDatasetDelete(id: any) {
    this.datasetService.deleteDataset(id).subscribe({
      next: () => {
        window.location.reload();
      },
      error: this.notification.defaultErrorHandler
    });
  }
}

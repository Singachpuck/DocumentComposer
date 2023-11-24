import {File} from "./file";

export class ComposedDocument extends File {

  static {
    File.ignore.push('datasetId', 'templateId');
  }

  datasetId: number | null = null;

  templateId: number | null = null;
}

import {Injectable} from '@angular/core';
import {Buffer} from 'buffer';
import {Template} from "../_model/template";

const API_ENDPOINT = 'http://localhost:8080/api/v1/';
const TEMPLATE_SUPPORTED_FORMATS = [
  {name: 'DOCX', media: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', iconClass: 'bi-filetype-docx'}
];

const DATASET_SUPPORTED_FORMATS = [
  {name: 'JSON', media: 'application/json', iconClass: 'bi-filetype-json'}
];

const DEFAULT_TOKEN_PLACEHOLDER = {
  begin: '${',
  end: '}'
}

const DEFAULT_ESCAPE_PLACEHOLDER = {
  begin: '\\',
  end: '\\'
}

@Injectable({
  providedIn: 'root'
})
export class UtilService {

  constructor() { }

  public encodeBase64(data: string) {
    return Buffer.from(data).toString('base64');
  }

  public decodeBase64(data: string) {
    return Buffer.from(data, 'base64').toString('binary');
  }

  public toBase64(file: File) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onloadend = () => {
        let base64String: any = reader.result;
        base64String = base64String.substring(base64String.indexOf(',') + 1);
        resolve(base64String);
      };
      reader.onerror = reject;
    })
  };

  public sortEntities(collection: Array<any>, type: 'name' | 'created' | 'size', order: 'asc' | 'desc') {
    if (type === 'name') {
      // @ts-ignore
      collection?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return a.name?.localeCompare(b.name) * ('asc' === order ? 1 : -1);
      });
    } else if (type === 'created') {
      collection?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return (new Date(a.created).getTime() - new Date(b.created).getTime()) * ('asc' === order ? 1 : -1);
      });
    } else if (type === 'size') {
      collection?.sort((a: Template, b: Template) => {
        // @ts-ignore
        return (a.size - b.size) * ('asc' === order ? 1 : -1);
      });
    }
  }

  // https://stackoverflow.com/questions/54753021/how-can-i-pass-an-auth-token-when-downloading-a-file
  public triggerDownload(content: Blob, name: string) {
    const url = URL.createObjectURL(content);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = name || 'file';

    anchor.click();
    URL.revokeObjectURL(url);
  }
}

export {
  API_ENDPOINT,
  TEMPLATE_SUPPORTED_FORMATS,
  DATASET_SUPPORTED_FORMATS,
  DEFAULT_TOKEN_PLACEHOLDER,
  DEFAULT_ESCAPE_PLACEHOLDER
}

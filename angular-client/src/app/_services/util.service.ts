import { Injectable } from '@angular/core';
import { Buffer } from 'buffer';

const API_ENDPOINT = 'http://localhost:8080/api/v1/';
const TEMPLATE_SUPPORTED_FORMATS = [
  {name: 'DOCX', media: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}
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
}

export {
  API_ENDPOINT,
  TEMPLATE_SUPPORTED_FORMATS,
  DEFAULT_TOKEN_PLACEHOLDER,
  DEFAULT_ESCAPE_PLACEHOLDER
}

import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_ENDPOINT} from "./util.service";

@Injectable({
  providedIn: 'root'
})
export class DownloadService {

  constructor(private http: HttpClient) { }

  downloadTemplate(id: number) {
    return this.http.get(API_ENDPOINT + 'download/templates/' + id, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  downloadDataset(id: number) {
    return this.http.get(API_ENDPOINT + 'download/datasets/' + id, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  downloadComposed(id: number) {
    return this.http.get(API_ENDPOINT + 'download/compose/' + id, {
      responseType: 'blob',
      observe: 'response'
    });
  }
}

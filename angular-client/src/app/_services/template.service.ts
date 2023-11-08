import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Template} from "../_model/template";
import {API_ENDPOINT} from "./util.service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

  constructor(private http: HttpClient) { }

  createTemplate(template: Template) {
    return this.http.post(API_ENDPOINT + 'templates', template.toJson(), {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  getTemplates(): Observable<Array<Template>> {
    return this.http.get<Array<Template>>(API_ENDPOINT + 'templates');
  }
}

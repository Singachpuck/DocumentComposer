import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Template} from "../_model/template";
import {API_ENDPOINT} from "./util.service";
import {Observable} from "rxjs";
import {TokenStorageService} from "./token-storage.service";

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  createTemplate(template: Template) {
    return this.http.post(API_ENDPOINT + 'templates', template.toJson(), {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  getTemplates(): Observable<Array<Template>> {
    let username = this.tokenService.getUsername();
    return this.http.get<Array<Template>>(API_ENDPOINT + 'templates/user/' + username);
  }

  deleteTemplate(id: any) {
    return this.http.delete(API_ENDPOINT + 'templates/' + id);
  }
}

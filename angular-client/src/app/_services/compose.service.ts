import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Template} from "../_model/template";
import {API_ENDPOINT} from "./util.service";
import {Observable} from "rxjs";
import {TokenStorageService} from "./token-storage.service";
import {ComposedDocument} from "../_model/composed-document";

@Injectable({
  providedIn: 'root'
})
export class ComposeService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  composeDocument(templateId: number, datasetId: number): Observable<ComposedDocument> {
    return this.http.post<ComposedDocument>(API_ENDPOINT + 'compose', {
      templateId,
      datasetId
    });
  }

  getDocuments(): Observable<Array<ComposedDocument>> {
    let username = this.tokenService.getUsername();
    return this.http.get<Array<ComposedDocument>>(API_ENDPOINT + 'compose/user/' + username);
  }
}

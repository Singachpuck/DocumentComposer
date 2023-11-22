import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_ENDPOINT} from "./util.service";
import {Observable} from "rxjs";
import {Dataset} from "../_model/dataset";
import {TokenStorageService} from "./token-storage.service";

@Injectable({
  providedIn: 'root'
})
export class DatasetService {

  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  createDataset(dataset: Dataset) {
    return this.http.post(API_ENDPOINT + 'datasets', dataset.toJson(), {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  getDatasets(): Observable<Array<Dataset>> {
    let username = this.tokenService.getUsername();
    return this.http.get<Array<Dataset>>(API_ENDPOINT + 'datasets/user/' + username);
  }
}

import {File} from "./file";

export class Template extends File{

  beginTokenPlaceholder: null | string = null;

  endTokenPlaceholder: null | string = null;

  beginEscapePlaceholder: null | string = null;

  endEscapePlaceholder: null | string = null;
}

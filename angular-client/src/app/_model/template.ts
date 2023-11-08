export class Template {

  id: null | number = null;

  name: null | string = null;

  format: null | string = null;

  created: null | Date = null;

  beginTokenPlaceholder: null | string = null;

  endTokenPlaceholder: null | string = null;

  beginEscapePlaceholder: null | string = null;

  endEscapePlaceholder: null | string = null;

  size: null | number = null;

  bytes: null | string = null;

  toJson(): { [name: string]: any } {
    let json = {};
    let ignore = ['size', 'created', 'id'];

    Object.getOwnPropertyNames(this).filter(name => ignore.indexOf(name) < 0).forEach(name => {
      // @ts-ignore
      json[name] = this[name];
    });

    return json;
  }
}

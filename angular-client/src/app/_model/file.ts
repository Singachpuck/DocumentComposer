export class File {

  protected static ignore = ['size', 'created', 'id'];

  id: null | number = null;

  name: null | string = null;

  format: null | string = null;

  created: null | Date = null;

  size: null | number = null;

  bytes: null | string = null;

  toJson(): { [name: string]: any } {
    let json = {};

    Object.getOwnPropertyNames(this).filter(name => File.ignore.indexOf(name) < 0).forEach(name => {
      // @ts-ignore
      json[name] = this[name];
    });

    return json;
  }
}

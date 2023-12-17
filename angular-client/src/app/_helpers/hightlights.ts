import {HIGHLIGHT_OPTIONS} from "ngx-highlightjs";

export const highlightsProvider = {
  provide: HIGHLIGHT_OPTIONS,
  useValue: {
    coreLibraryLoader: () => import('highlight.js/lib/core'),
    languages: {
      text: () => import('highlight.js/lib/languages/plaintext')
    }
  }
}

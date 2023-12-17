import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {

  placeholdersExample =
`// Default example
\${var1}

// But you can configure
(var2)

// Bad example, but it works
)var3)

// Don't limit yourself!
#%@!var4||^&`;

  basicExample =
`a + b // Addition
a - b // Subtraction
a * b // Multiplication
a / b // Division
`;

  groupExample =
`a * (b + c)
c / a * (b + d)
((c - (b + a)) / d)
(a + b) / (d - c)`;

  literalsExample =
`a / 100
2 * b
c + "%"
1.5 + 3.5 - 10`;

  constructor() { }

  ngOnInit(): void {
  }

}

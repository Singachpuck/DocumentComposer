import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  year = 2023;
  ownerEmail = 'ochkasdmytro@gmail.com';
  ownerGithub = 'https://github.com/Singachpuck/DocumentComposer';
  ownerLinkedIn = 'https://www.linkedin.com/in/dmytro-ochkas/';

  constructor() { }

  ngOnInit(): void {
  }

}

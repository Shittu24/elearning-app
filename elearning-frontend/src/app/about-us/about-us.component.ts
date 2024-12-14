import { Component } from '@angular/core';
import { faGoogle, faLinkedin, faTwitter } from '@fortawesome/free-brands-svg-icons';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css']
})
export class AboutUsComponent {
  faGoogle = faGoogle;
  faLinkedin = faLinkedin;
  faTwitter = faTwitter;

  constructor(private library: FaIconLibrary) {
    // Add the icons to the library
    library.addIcons(faGoogle, faLinkedin, faTwitter);
  }
}

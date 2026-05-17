import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { MoradiaCardComponent } from './components/moradia-card-component/moradia-card-component';

@Component({
  selector: 'app-feed-colega',
  imports: [RouterOutlet, TopNavbarComponent, MoradiaCardComponent],
  templateUrl: './feed-colega.html',
  styleUrl: './feed-colega.css',
})
export class FeedColega {

}

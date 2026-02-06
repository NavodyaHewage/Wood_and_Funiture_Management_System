import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from './components/shared/toast/toast.component';
import { SessionService } from './service/session.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'Wood-and-Funiture-Management-System-FrontEnd';

  constructor(private sessionService: SessionService) { }

  ngOnInit() {
    this.sessionService.startMonitoring();
  }
}

import { Injectable } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { BehaviorSubject, filter, map } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class NavService {
    private titleSubject = new BehaviorSubject<string>('Dashboard');
    public title$ = this.titleSubject.asObservable();

    constructor(private router: Router, private activatedRoute: ActivatedRoute) {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(() => {
                let route = this.activatedRoute.firstChild;
                while (route?.firstChild) {
                    route = route.firstChild;
                }
                return route?.snapshot.data['title'] || 'Dashboard';
            })
        ).subscribe(title => {
            this.titleSubject.next(title);
        });
    }

    setTitle(title: string) {
        this.titleSubject.next(title);
    }
}

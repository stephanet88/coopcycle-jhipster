import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { UserAccountService } from '../service/user-account.service';

import { UserAccountComponent } from './user-account.component';

describe('Component Tests', () => {
  describe('UserAccount Management Component', () => {
    let comp: UserAccountComponent;
    let fixture: ComponentFixture<UserAccountComponent>;
    let service: UserAccountService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserAccountComponent],
      })
        .overrideTemplate(UserAccountComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserAccountComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(UserAccountService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.userAccounts?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});

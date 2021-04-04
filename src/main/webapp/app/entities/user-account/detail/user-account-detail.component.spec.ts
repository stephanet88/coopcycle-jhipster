import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserAccountDetailComponent } from './user-account-detail.component';

describe('Component Tests', () => {
  describe('UserAccount Management Detail Component', () => {
    let comp: UserAccountDetailComponent;
    let fixture: ComponentFixture<UserAccountDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [UserAccountDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ userAccount: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(UserAccountDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UserAccountDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load userAccount on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.userAccount).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});

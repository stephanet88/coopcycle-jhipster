import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { InstitutionDetailComponent } from './institution-detail.component';

describe('Component Tests', () => {
  describe('Institution Management Detail Component', () => {
    let comp: InstitutionDetailComponent;
    let fixture: ComponentFixture<InstitutionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [InstitutionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ institution: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(InstitutionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InstitutionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load institution on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.institution).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { InstitutionService } from '../service/institution.service';

import { InstitutionComponent } from './institution.component';

describe('Component Tests', () => {
  describe('Institution Management Component', () => {
    let comp: InstitutionComponent;
    let fixture: ComponentFixture<InstitutionComponent>;
    let service: InstitutionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InstitutionComponent],
      })
        .overrideTemplate(InstitutionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InstitutionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(InstitutionService);

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
      expect(comp.institutions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});

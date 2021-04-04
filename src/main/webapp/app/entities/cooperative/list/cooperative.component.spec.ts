import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CooperativeService } from '../service/cooperative.service';

import { CooperativeComponent } from './cooperative.component';

describe('Component Tests', () => {
  describe('Cooperative Management Component', () => {
    let comp: CooperativeComponent;
    let fixture: ComponentFixture<CooperativeComponent>;
    let service: CooperativeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CooperativeComponent],
      })
        .overrideTemplate(CooperativeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CooperativeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CooperativeService);

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
      expect(comp.cooperatives?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});

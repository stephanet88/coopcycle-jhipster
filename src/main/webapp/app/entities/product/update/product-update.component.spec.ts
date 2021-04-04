jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProductService } from '../service/product.service';
import { IProduct, Product } from '../product.model';
import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';

import { ProductUpdateComponent } from './product-update.component';

describe('Component Tests', () => {
  describe('Product Management Update Component', () => {
    let comp: ProductUpdateComponent;
    let fixture: ComponentFixture<ProductUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let productService: ProductService;
    let institutionService: InstitutionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProductUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProductUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProductUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      productService = TestBed.inject(ProductService);
      institutionService = TestBed.inject(InstitutionService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Institution query and add missing value', () => {
        const product: IProduct = { id: 456 };
        const institution: IInstitution = { id: 83633 };
        product.institution = institution;

        const institutionCollection: IInstitution[] = [{ id: 1184 }];
        spyOn(institutionService, 'query').and.returnValue(of(new HttpResponse({ body: institutionCollection })));
        const additionalInstitutions = [institution];
        const expectedCollection: IInstitution[] = [...additionalInstitutions, ...institutionCollection];
        spyOn(institutionService, 'addInstitutionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ product });
        comp.ngOnInit();

        expect(institutionService.query).toHaveBeenCalled();
        expect(institutionService.addInstitutionToCollectionIfMissing).toHaveBeenCalledWith(
          institutionCollection,
          ...additionalInstitutions
        );
        expect(comp.institutionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const product: IProduct = { id: 456 };
        const institution: IInstitution = { id: 82793 };
        product.institution = institution;

        activatedRoute.data = of({ product });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(product));
        expect(comp.institutionsSharedCollection).toContain(institution);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = { id: 123 };
        spyOn(productService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: product }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(productService.update).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = new Product();
        spyOn(productService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: product }));
        saveSubject.complete();

        // THEN
        expect(productService.create).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = { id: 123 };
        spyOn(productService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(productService.update).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackInstitutionById', () => {
        it('Should return tracked Institution primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackInstitutionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});

jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { InstitutionService } from '../service/institution.service';
import { IInstitution, Institution } from '../institution.model';
import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { UserAccountService } from 'app/entities/user-account/service/user-account.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';
import { CooperativeService } from 'app/entities/cooperative/service/cooperative.service';

import { InstitutionUpdateComponent } from './institution-update.component';

describe('Component Tests', () => {
  describe('Institution Management Update Component', () => {
    let comp: InstitutionUpdateComponent;
    let fixture: ComponentFixture<InstitutionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let institutionService: InstitutionService;
    let userAccountService: UserAccountService;
    let cartService: CartService;
    let cooperativeService: CooperativeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InstitutionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(InstitutionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InstitutionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      institutionService = TestBed.inject(InstitutionService);
      userAccountService = TestBed.inject(UserAccountService);
      cartService = TestBed.inject(CartService);
      cooperativeService = TestBed.inject(CooperativeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call userAccount query and add missing value', () => {
        const institution: IInstitution = { id: 456 };
        const userAccount: IUserAccount = { id: 31164 };
        institution.userAccount = userAccount;

        const userAccountCollection: IUserAccount[] = [{ id: 69727 }];
        spyOn(userAccountService, 'query').and.returnValue(of(new HttpResponse({ body: userAccountCollection })));
        const expectedCollection: IUserAccount[] = [userAccount, ...userAccountCollection];
        spyOn(userAccountService, 'addUserAccountToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        expect(userAccountService.query).toHaveBeenCalled();
        expect(userAccountService.addUserAccountToCollectionIfMissing).toHaveBeenCalledWith(userAccountCollection, userAccount);
        expect(comp.userAccountsCollection).toEqual(expectedCollection);
      });

      it('Should call Cart query and add missing value', () => {
        const institution: IInstitution = { id: 456 };
        const carts: ICart[] = [{ id: 26855 }];
        institution.carts = carts;

        const cartCollection: ICart[] = [{ id: 62035 }];
        spyOn(cartService, 'query').and.returnValue(of(new HttpResponse({ body: cartCollection })));
        const additionalCarts = [...carts];
        const expectedCollection: ICart[] = [...additionalCarts, ...cartCollection];
        spyOn(cartService, 'addCartToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        expect(cartService.query).toHaveBeenCalled();
        expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, ...additionalCarts);
        expect(comp.cartsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Cooperative query and add missing value', () => {
        const institution: IInstitution = { id: 456 };
        const cooperative: ICooperative = { id: 14278 };
        institution.cooperative = cooperative;

        const cooperativeCollection: ICooperative[] = [{ id: 28447 }];
        spyOn(cooperativeService, 'query').and.returnValue(of(new HttpResponse({ body: cooperativeCollection })));
        const additionalCooperatives = [cooperative];
        const expectedCollection: ICooperative[] = [...additionalCooperatives, ...cooperativeCollection];
        spyOn(cooperativeService, 'addCooperativeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        expect(cooperativeService.query).toHaveBeenCalled();
        expect(cooperativeService.addCooperativeToCollectionIfMissing).toHaveBeenCalledWith(
          cooperativeCollection,
          ...additionalCooperatives
        );
        expect(comp.cooperativesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const institution: IInstitution = { id: 456 };
        const userAccount: IUserAccount = { id: 29959 };
        institution.userAccount = userAccount;
        const carts: ICart = { id: 13761 };
        institution.carts = [carts];
        const cooperative: ICooperative = { id: 28091 };
        institution.cooperative = cooperative;

        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(institution));
        expect(comp.userAccountsCollection).toContain(userAccount);
        expect(comp.cartsSharedCollection).toContain(carts);
        expect(comp.cooperativesSharedCollection).toContain(cooperative);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const institution = { id: 123 };
        spyOn(institutionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: institution }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(institutionService.update).toHaveBeenCalledWith(institution);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const institution = new Institution();
        spyOn(institutionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: institution }));
        saveSubject.complete();

        // THEN
        expect(institutionService.create).toHaveBeenCalledWith(institution);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const institution = { id: 123 };
        spyOn(institutionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ institution });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(institutionService.update).toHaveBeenCalledWith(institution);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserAccountById', () => {
        it('Should return tracked UserAccount primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserAccountById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCartById', () => {
        it('Should return tracked Cart primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCartById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCooperativeById', () => {
        it('Should return tracked Cooperative primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCooperativeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedCart', () => {
        it('Should return option if no Cart is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedCart(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Cart for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedCart(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Cart is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedCart(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});

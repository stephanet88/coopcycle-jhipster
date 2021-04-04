import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPaymentOption, PaymentOption } from '../payment-option.model';

import { PaymentOptionService } from './payment-option.service';

describe('Service Tests', () => {
  describe('PaymentOption Service', () => {
    let service: PaymentOptionService;
    let httpMock: HttpTestingController;
    let elemDefault: IPaymentOption;
    let expectedResult: IPaymentOption | IPaymentOption[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PaymentOptionService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        type: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a PaymentOption', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new PaymentOption()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a PaymentOption', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a PaymentOption', () => {
        const patchObject = Object.assign({}, new PaymentOption());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of PaymentOption', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a PaymentOption', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPaymentOptionToCollectionIfMissing', () => {
        it('should add a PaymentOption to an empty array', () => {
          const paymentOption: IPaymentOption = { id: 123 };
          expectedResult = service.addPaymentOptionToCollectionIfMissing([], paymentOption);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(paymentOption);
        });

        it('should not add a PaymentOption to an array that contains it', () => {
          const paymentOption: IPaymentOption = { id: 123 };
          const paymentOptionCollection: IPaymentOption[] = [
            {
              ...paymentOption,
            },
            { id: 456 },
          ];
          expectedResult = service.addPaymentOptionToCollectionIfMissing(paymentOptionCollection, paymentOption);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a PaymentOption to an array that doesn't contain it", () => {
          const paymentOption: IPaymentOption = { id: 123 };
          const paymentOptionCollection: IPaymentOption[] = [{ id: 456 }];
          expectedResult = service.addPaymentOptionToCollectionIfMissing(paymentOptionCollection, paymentOption);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(paymentOption);
        });

        it('should add only unique PaymentOption to an array', () => {
          const paymentOptionArray: IPaymentOption[] = [{ id: 123 }, { id: 456 }, { id: 52626 }];
          const paymentOptionCollection: IPaymentOption[] = [{ id: 123 }];
          expectedResult = service.addPaymentOptionToCollectionIfMissing(paymentOptionCollection, ...paymentOptionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const paymentOption: IPaymentOption = { id: 123 };
          const paymentOption2: IPaymentOption = { id: 456 };
          expectedResult = service.addPaymentOptionToCollectionIfMissing([], paymentOption, paymentOption2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(paymentOption);
          expect(expectedResult).toContain(paymentOption2);
        });

        it('should accept null and undefined values', () => {
          const paymentOption: IPaymentOption = { id: 123 };
          expectedResult = service.addPaymentOptionToCollectionIfMissing([], null, paymentOption, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(paymentOption);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});

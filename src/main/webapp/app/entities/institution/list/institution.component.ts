import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInstitution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';
import { InstitutionDeleteDialogComponent } from '../delete/institution-delete-dialog.component';

@Component({
  selector: 'jhi-institution',
  templateUrl: './institution.component.html',
})
export class InstitutionComponent implements OnInit {
  institutions?: IInstitution[];
  isLoading = false;

  constructor(protected institutionService: InstitutionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.institutionService.query().subscribe(
      (res: HttpResponse<IInstitution[]>) => {
        this.isLoading = false;
        this.institutions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IInstitution): number {
    return item.id!;
  }

  delete(institution: IInstitution): void {
    const modalRef = this.modalService.open(InstitutionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.institution = institution;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

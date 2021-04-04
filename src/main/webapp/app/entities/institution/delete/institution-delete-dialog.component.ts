import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInstitution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';

@Component({
  templateUrl: './institution-delete-dialog.component.html',
})
export class InstitutionDeleteDialogComponent {
  institution?: IInstitution;

  constructor(protected institutionService: InstitutionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.institutionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

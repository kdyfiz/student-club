import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getClubs } from 'app/entities/club/club.reducer';
import { RegistrationStatus } from 'app/shared/model/enumerations/registration-status.model';
import { createEntity, getEntity, reset, updateEntity } from './student-club-registration.reducer';

export const StudentClubRegistrationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clubs = useAppSelector(state => state.club.entities);
  const studentClubRegistrationEntity = useAppSelector(state => state.studentClubRegistration.entity);
  const loading = useAppSelector(state => state.studentClubRegistration.loading);
  const updating = useAppSelector(state => state.studentClubRegistration.updating);
  const updateSuccess = useAppSelector(state => state.studentClubRegistration.updateSuccess);
  const registrationStatusValues = Object.keys(RegistrationStatus);

  const handleClose = () => {
    navigate(`/student-club-registration${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getClubs({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.registrationDate = convertDateTimeToServer(values.registrationDate);

    const entity = {
      ...studentClubRegistrationEntity,
      ...values,
      club: clubs.find(it => it.id.toString() === values.club?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          registrationDate: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...studentClubRegistrationEntity,
          registrationDate: convertDateTimeFromServer(studentClubRegistrationEntity.registrationDate),
          club: studentClubRegistrationEntity?.club?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="registerClubApp.studentClubRegistration.home.createOrEditLabel" data-cy="StudentClubRegistrationCreateUpdateHeading">
            <Translate contentKey="registerClubApp.studentClubRegistration.home.createOrEditLabel">
              Create or edit a StudentClubRegistration
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="student-club-registration-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('registerClubApp.studentClubRegistration.registrationDate')}
                id="student-club-registration-registrationDate"
                name="registrationDate"
                data-cy="registrationDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('registerClubApp.studentClubRegistration.status')}
                id="student-club-registration-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {registrationStatusValues.map(registrationStatus => (
                  <option value={registrationStatus} key={registrationStatus}>
                    {translate(`registerClubApp.RegistrationStatus.${registrationStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="student-club-registration-club"
                name="club"
                data-cy="club"
                label={translate('registerClubApp.studentClubRegistration.club')}
                type="select"
              >
                <option value="" key="0" />
                {clubs
                  ? clubs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/student-club-registration" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StudentClubRegistrationUpdate;

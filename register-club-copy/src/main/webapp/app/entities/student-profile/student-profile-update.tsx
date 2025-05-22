import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getStudentClubRegistrations } from 'app/entities/student-club-registration/student-club-registration.reducer';
import { createEntity, getEntity, reset, updateEntity } from './student-profile.reducer';

export const StudentProfileUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const studentClubRegistrations = useAppSelector(state => state.studentClubRegistration.entities);
  const studentProfileEntity = useAppSelector(state => state.studentProfile.entity);
  const loading = useAppSelector(state => state.studentProfile.loading);
  const updating = useAppSelector(state => state.studentProfile.updating);
  const updateSuccess = useAppSelector(state => state.studentProfile.updateSuccess);

  const handleClose = () => {
    navigate('/student-profile');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getStudentClubRegistrations({}));
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

    const entity = {
      ...studentProfileEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      studentClubRegistration: studentClubRegistrations.find(it => it.id.toString() === values.studentClubRegistration?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...studentProfileEntity,
          user: studentProfileEntity?.user?.id,
          studentClubRegistration: studentProfileEntity?.studentClubRegistration?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="registerClubApp.studentProfile.home.createOrEditLabel" data-cy="StudentProfileCreateUpdateHeading">
            <Translate contentKey="registerClubApp.studentProfile.home.createOrEditLabel">Create or edit a StudentProfile</Translate>
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
                  id="student-profile-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('registerClubApp.studentProfile.studentId')}
                id="student-profile-studentId"
                name="studentId"
                data-cy="studentId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('registerClubApp.studentProfile.fullName')}
                id="student-profile-fullName"
                name="fullName"
                data-cy="fullName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('registerClubApp.studentProfile.grade')}
                id="student-profile-grade"
                name="grade"
                data-cy="grade"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="student-profile-user"
                name="user"
                data-cy="user"
                label={translate('registerClubApp.studentProfile.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="student-profile-studentClubRegistration"
                name="studentClubRegistration"
                data-cy="studentClubRegistration"
                label={translate('registerClubApp.studentProfile.studentClubRegistration')}
                type="select"
              >
                <option value="" key="0" />
                {studentClubRegistrations
                  ? studentClubRegistrations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/student-profile" replace color="info">
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

export default StudentProfileUpdate;

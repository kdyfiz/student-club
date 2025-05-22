import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './student-club-registration.reducer';

export const StudentClubRegistrationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const studentClubRegistrationEntity = useAppSelector(state => state.studentClubRegistration.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="studentClubRegistrationDetailsHeading">
          <Translate contentKey="registerClubApp.studentClubRegistration.detail.title">StudentClubRegistration</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{studentClubRegistrationEntity.id}</dd>
          <dt>
            <span id="registrationDate">
              <Translate contentKey="registerClubApp.studentClubRegistration.registrationDate">Registration Date</Translate>
            </span>
          </dt>
          <dd>
            {studentClubRegistrationEntity.registrationDate ? (
              <TextFormat value={studentClubRegistrationEntity.registrationDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="registerClubApp.studentClubRegistration.status">Status</Translate>
            </span>
          </dt>
          <dd>{studentClubRegistrationEntity.status}</dd>
          <dt>
            <Translate contentKey="registerClubApp.studentClubRegistration.club">Club</Translate>
          </dt>
          <dd>{studentClubRegistrationEntity.club ? studentClubRegistrationEntity.club.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/student-club-registration" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/student-club-registration/${studentClubRegistrationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StudentClubRegistrationDetail;

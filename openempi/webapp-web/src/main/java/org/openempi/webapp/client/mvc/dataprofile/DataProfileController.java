/**
 *
 * Copyright (C) 2002-2012 "SYSNET International, Inc."
 * support@sysnetint.com [http://www.sysnetint.com]
 *
 * This file is part of OpenEMPI.
 *
 * OpenEMPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openempi.webapp.client.mvc.dataprofile;

import java.util.List;

import org.openempi.webapp.client.AppEvents;
import org.openempi.webapp.client.ProfileDataServiceAsync;
import org.openempi.webapp.client.domain.AuthenticationException;
import org.openempi.webapp.client.model.DataProfileWeb;
import org.openempi.webapp.client.model.DataProfileAttributeWeb;
import org.openempi.webapp.client.model.DataProfileAttributeValueWeb;
import org.openempi.webapp.client.mvc.Controller;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataProfileController extends Controller
{
    private static final int topCount = 50;

	private DataProfileView dataProfileView;
    private DataProfileListView dataProfileListView;

	public DataProfileController() {

		this.registerEventTypes(AppEvents.DataProfileView);
	    this.registerEventTypes(AppEvents.DataProfileListView);
	}

	public void initialize() {
		dataProfileView = new DataProfileView(this);
	    dataProfileListView = new DataProfileListView(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		EventType type = event.getType();
	    if (type == AppEvents.DataProfileListView) {

	        forwardToView(dataProfileListView, event);

        } else if (type == AppEvents.DataProfileListRequest) {

            requestProfileListData();

        } else if (type == AppEvents.DataProfileDelete) {

            Integer dataProfileId = event.getData();
            removeDataProfile(dataProfileId);

	    } else if (type == AppEvents.DataProfileView) {

			forwardToView(dataProfileView, event);

		} else if (type == AppEvents.DataProfileAttributeRequest) {

			// Data profile attributes can be coming from difference data profile
	        Integer dataProfileId = event.getData();
			requestProfileAttributeData(dataProfileId);

		} else if (type == AppEvents.DataProfileAttributeValueRequest) {

			DataProfileAttributeWeb attribute = event.getData();
			requestProfileAttributeValueData(attribute, topCount);
		}
	}

    private void requestProfileListData() {
        ProfileDataServiceAsync profileDataService = getProfileDataService();
        profileDataService.getDataProfiles(new AsyncCallback<List<DataProfileWeb>>() {

          public void onFailure(Throwable caught) {
              // Info.display("Information", "onFailure."+caught.getMessage());
              if (caught instanceof AuthenticationException) {
                  Dispatcher.get().dispatch(AppEvents.Logout);
                  return;
              }
              forwardToView(dataProfileListView, AppEvents.Error, caught.getMessage());
          }

          public void onSuccess(List<DataProfileWeb> result) {
            // Info.display("Information", "onSuccess."+result.size());
            forwardToView(dataProfileListView, AppEvents.DataProfileListReceived, result);
          }
        });
    }

    private void removeDataProfile(Integer dataProfileId) {
        ProfileDataServiceAsync profileDataService = getProfileDataService();
        profileDataService.removeDataProfile(dataProfileId, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {

                if (caught instanceof AuthenticationException) {

                    forwardToView(dataProfileListView, AppEvents.Logout, null);
                    return;
                }
                forwardToView(dataProfileListView, AppEvents.Error, caught.getMessage());
            }

            public void onSuccess(String message) {
              // Info.display("Information", "delete onSuccess.");
              forwardToView(dataProfileListView, AppEvents.DataProfileDeleteComplete, message);
            }
          });
    }

	private void requestProfileAttributeData(Integer dataResource) {
		ProfileDataServiceAsync profileDataService = getProfileDataService();
		profileDataService.getDataProfileAttributes(dataResource, new AsyncCallback<List<DataProfileAttributeWeb>>() {

	      public void onFailure(Throwable caught) {
			  // Info.display("Information", "onFailure."+caught.getMessage());
	    	  if (caught instanceof AuthenticationException) {
	    		  Dispatcher.get().dispatch(AppEvents.Logout);
	    		  return;
	    	  }
	    	  forwardToView(dataProfileView, AppEvents.Error, caught.getMessage());
	      }

	      public void onSuccess(List<DataProfileAttributeWeb> result) {
			// Info.display("Information", "onSuccess."+result.size());
	        forwardToView(dataProfileView, AppEvents.DataProfileAttributeReceived, result);
	      }
	    });
	}

	private void requestProfileAttributeValueData(DataProfileAttributeWeb attribute, int topCount) {

		ProfileDataServiceAsync profileDataService = getProfileDataService();
		profileDataService.getDataProfileAttributeValues(attribute.getAttributeId(), topCount,  new AsyncCallback<List<DataProfileAttributeValueWeb>>() {

		      public void onFailure(Throwable caught) {
				  // Info.display("Information", "onFailure."+caught.getMessage());
		    	  if (caught instanceof AuthenticationException) {
		    		  Dispatcher.get().dispatch(AppEvents.Logout);
		    		  return;
		    	  }
		    	  forwardToView(dataProfileView, AppEvents.Error, caught.getMessage());
		      }

		      public void onSuccess(List<DataProfileAttributeValueWeb> result) {
				// Info.display("Information", "onSuccess."+result.size());
		        forwardToView(dataProfileView, AppEvents.DataProfileAttributeValueReceived, result);
		      }
		    });

	}
}

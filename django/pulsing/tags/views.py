"""
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

@author Ji Kim
"""

from django.http import JsonResponse, HttpResponseBadRequest

import json
import logging
import uuid
import datetime

from .models import (pulseSearch, userSearch) 

logger = logging.getLogger(__name__)
elasticDict = dict(pulse=pulseSearch, user=userSearch)

def searchDocument(request):
    logger.debug('searchDocument')
    
    parameters = [field for field in ['search', 'index', 'doc_type'] if not field in request.GET]
    
    if parameters.length > 0:
        logger.debug('searchDocument lacking parameters - %s', parameters)
        return HttpResponseBadRequest()
    
    logger.debug('searchDocument querying %s : %s - %s', request.GET['index'], request.GET['doc_type'], request.GET['search'])
    
    search = json.loads(request.GET['search'])
    doc_type = request.GET['doc_type']
    elastic = elasticDict[request.GET['index']]
    # for now just name, later pass the field
    result = elastic.search(doc_type, search)

    logger.debug('searchDocument query result - %s', result)
    
    if result['timed_out']:
        return JsonResponse({'code': 'FAILURE', 'data': {}, 'message': 'Timed out in the search'})
    else:
        return JsonResponse({
            'code': 'SUCCESS',
            'data': {'result': result['hits']['hits']},
            'message': ''
        })
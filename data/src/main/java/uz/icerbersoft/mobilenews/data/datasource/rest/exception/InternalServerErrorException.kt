package uz.icerbersoft.mobilenews.data.datasource.rest.exception

import uz.icerbersoft.mobilenews.data.datasource.rest.retrofit.exception.RestErrorException
import uz.icerbersoft.mobilenews.data.datasource.rest.retrofit.interceptor.HttpErrorResponse

class InternalServerErrorException(
    override val response: HttpErrorResponse
) : RestErrorException(response)
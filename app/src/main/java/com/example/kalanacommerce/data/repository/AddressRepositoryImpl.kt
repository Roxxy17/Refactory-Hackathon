package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import com.example.kalanacommerce.data.remote.service.AddressService
import com.example.kalanacommerce.domain.model.Address
import com.example.kalanacommerce.domain.repository.AddressRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AddressRepositoryImpl(
    private val api: AddressService
) : AddressRepository {

    override fun getAddresses(): Flow<Resource<List<Address>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAddresses()
            val dtos = response.data ?: emptyList()

            val domainData = dtos.map { dto ->
                Address(
                    id = dto.id,
                    label = dto.label,
                    recipientName = dto.recipientName,
                    phoneNumber = dto.phoneNumber,
                    street = dto.street,
                    postalCode = dto.postalCode,
                    provincesId = dto.provincesId ?: "",
                    citiesId = dto.citiesId ?: "",
                    districtsId = dto.districtsId ?: "",
                    fullAddress = "${dto.street}, ${dto.citiesId ?: ""} ${dto.postalCode}",
                    isDefault = dto.isDefault
                )
            }
            emit(Resource.Success(domainData))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    override fun getAddressById(id: String): Flow<Resource<Address>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAddressById(id)
            val dto = response.data

            if (dto != null) {
                val address = Address(
                    id = dto.id,
                    label = dto.label,
                    recipientName = dto.recipientName,
                    phoneNumber = dto.phoneNumber,
                    street = dto.street,
                    postalCode = dto.postalCode,
                    provincesId = dto.provincesId ?: "",
                    citiesId = dto.citiesId ?: "",
                    districtsId = dto.districtsId ?: "",
                    fullAddress = "${dto.street}, ${dto.citiesId ?: ""} ${dto.postalCode}",
                    isDefault = dto.isDefault
                )
                emit(Resource.Success(address))
            } else {
                emit(Resource.Error("Alamat tidak ditemukan."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    override fun createAddress(request: AddressRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            api.createAddress(request)
            emit(Resource.Success("Alamat berhasil ditambahkan"))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    override fun updateAddress(id: String, request: AddressRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            api.updateAddress(id, request)
            emit(Resource.Success("Alamat berhasil diupdate"))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    override fun deleteAddress(id: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            api.deleteAddress(id)
            emit(Resource.Success("Alamat berhasil dihapus"))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    // --- HELPER ERROR MESSAGE (Sama seperti Profile) ---
    private fun getDetailedErrorMessage(e: Exception): String {
        return when (e) {
            is ClientRequestException -> {
                when (e.response.status.value) {
                    400, 422 -> "Data alamat tidak valid. Cek inputan."
                    401 -> "Sesi habis. Silakan login ulang."
                    404 -> "Alamat tidak ditemukan."
                    else -> "Gagal memproses permintaan."
                }
            }
            is ServerResponseException -> "Server sedang sibuk/error (500)."
            is RedirectResponseException -> "Kesalahan jaringan (Redirect)."
            is TimeoutException, is SocketTimeoutException -> "Waktu habis. Koneksi internet lambat."
            is IOException -> "Gagal terhubung. Cek koneksi internet."
            else -> "Terjadi kesalahan." // Pesan singkat tanpa detail teknis
        }
    }
}
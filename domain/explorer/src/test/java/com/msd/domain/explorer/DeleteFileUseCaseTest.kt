package com.msd.domain.explorer

import com.msd.domain.explorer.model.NetworkFile
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class DeleteFileUseCaseTest {

    private val repository: IExplorerRepository = mock()

    private val useCase = DeleteFileUseCase(repository)

    @Test
    fun `when deleting file should invoke the repository`() {
        val file = NetworkFile(
            name = "name",
            path = "path",
            localPath = "localPath",
            isLocal = false
        )

        useCase.invoke(file)

        verify(repository).deleteFile(localFilePath = "localPath", fileName = "name")
        verifyNoMoreInteractions(repository)
    }
}

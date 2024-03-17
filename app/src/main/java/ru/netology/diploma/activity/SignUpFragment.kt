package ru.netology.diploma.activity

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentSignUpBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.model.AttachmentModel
import ru.netology.diploma.viewmodel.SignUpViewModel
import java.io.File
import java.util.Locale

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModelSignUp: SignUpViewModel by activityViewModels()

    private val photoResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult
            val file = uri.toFile()
            viewModelSignUp.setPhoto(uri, file, AttachmentType.IMAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signUpEnter.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val username = binding.userName.text.toString()
                val login = binding.login.text.toString()
                val password = binding.password.text.toString()
                val passwordConfirm = binding.passwordConfirm.text.toString()

                binding.signUpEnter.isEnabled = !(username.isEmpty() || login.isEmpty() || password.isEmpty() || password.isEmpty() || password != passwordConfirm)
            }
        }

        with(binding) {
            userName.addTextChangedListener (textWatcher)
            login.addTextChangedListener (textWatcher)
            password.addTextChangedListener (textWatcher)
            passwordConfirm.addTextChangedListener (textWatcher)
        }


        with(binding) {

            signUpEnter.setOnClickListener {
                if (viewModelSignUp.photo.value == null) {
                    viewModelSignUp.registerAndSetAuth(
                        login.text.toString(),
                        password.text.toString(),
                        userName.text.toString()
                    )
                } else {
                    if (isImageValid(viewModelSignUp.photo.value!!)) {
                        viewModelSignUp.registerWithAvatarAndSetAuth(
                            login.text.toString(),
                            password.text.toString(),
                            userName.text.toString()
                        )
                    } else {
                        Snackbar.make(binding.scrollView, "", Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.signUpEnter)
                            .setTextMaxLines(3)
                            .setText("jpeg, png format only &\nthe maximum size is 2048*2048")
                            .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
                            .show()
                    }

                }

            }
        }

        viewModelSignUp.response.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
        }

        viewModelSignUp.error.observe(viewLifecycleOwner) {
            Snackbar.make(binding.signUpEnter, "", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.signUpEnter)
                .setTextMaxLines(3)
                .setText("Such user is already registered")
                .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
                .show()
        }

        // ***   для фото

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                //.provider(ImageProvider.CAMERA)
                .maxResultSize(1024, 1024)
                //.compress(2048)
                .createIntent { photoResultContract.launch(it) }
            //.createIntent(photoResultContract::launch)
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .compress(1024)
                .createIntent { photoResultContract.launch(it) }
        }

        binding.removePhoto.setOnClickListener {
            viewModelSignUp.clearPhoto()
        }

        viewModelSignUp.photo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.avatarPreview.setImageResource(R.drawable.ic_account_box_140)
                return@observe
            }

            binding.avatarPreview.setImageURI(it.uri)
        }



        return binding.root
    }

    private fun isImageValid(attachmentModel: AttachmentModel): Boolean {
        val allowedFormats = listOf("jpeg", "jpg", "png")
        val maxImageSize = 2048 // максимальный размер изображения

        // Проверка типа файла
        if (attachmentModel.type != AttachmentType.IMAGE) {
            return false
        }

        // Получение абсолютного пути к файлу из Uri
        val filePath = attachmentModel.uri.path!!
        val imageFile = File(filePath)

        // Проверка формата изображения
        val fileExtension = imageFile.extension.lowercase(Locale.ROOT)
        if (fileExtension !in allowedFormats) {
            return false
        }

        // Проверка размера изображения
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight

        if (imageWidth > maxImageSize || imageHeight > maxImageSize) {
            return false
        }

        return true
    }
}
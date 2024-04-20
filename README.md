### Image Classifier

This Android application is an image classifier that utilizes TensorFlow Lite to run a convolutional neural network (CNN) model for image classification. The app allows users to load images either from the camera or from the device's gallery and predicts the class of the image using the trained CNN model.

#### MainActivity

- **Description**: The `MainActivity` of the app provides a user interface to interact with the image classifier. Users can either click a picture using the device's camera or select an image from the gallery. After selecting an image, the app predicts its class using the trained CNN model and displays the predicted class on the screen.

- **Functionality**:
  - Click Picture: Allows users to capture an image using the device's camera.
  - Select Image from Gallery: Enables users to choose an image from the device's gallery.
  - Predict: Triggers the prediction process using the selected image and displays the predicted class.
  - Display Image: Shows the selected image on the screen for user reference.

#### TensorFlow Lite Integration

- **Model**: The app integrates a trained convolutional neural network (CNN) model for image classification. The model is loaded using TensorFlow Lite, allowing efficient execution on mobile devices.

- **Prediction**: Upon selecting an image, the app preprocesses the image, feeds it into the CNN model, and predicts its class. The predicted class is then displayed to the user.

#### Image Processing

- **Preprocessing**: The selected image undergoes preprocessing using TensorFlow Lite's image processing functionalities. It includes resizing the image to the required dimensions expected by the CNN model.

#### Usage

1. Launch the app on an Android device.
2. Click the "Click Picture" button to capture an image using the device's camera, or select the "Select Image from Gallery" button to choose an image from the device's gallery.
3. After selecting an image, click the "Predict" button to initiate the prediction process.
4. The predicted class of the image will be displayed on the screen.
5. Explore different images to classify and observe the predictions made by the image classifier.

#### TensorFlow Lite Model

- **Model**: The CNN model used for image classification is trained on a dataset containing images of different classes (e.g., Apple, Banana, Orange). The model is converted to the TensorFlow Lite format for deployment on mobile devices.

- **Classes**: The model predicts the class of the input image among the predefined classes, such as Apple, Banana, Orange, or "Cannot Predict" if the class is unknown.

import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import {FormLabel, Input, Alert, AlertIcon, Image, Box, Button, Stack, VStack} from "@chakra-ui/react";
import {updateCustomer, uploadCustomerProfilePicture} from "../../services/client.js"
import { successNotification, errorNotification } from "../../services/Notification.js"
import {useCallback, useEffect, useState} from "react";
import {useDropzone} from "react-dropzone";

//const [path, setPath] = useState([]);

let imageUrl = '';

const MyTextInput = ({ label, ...props }) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

const MyDropzone = ({customerId, fetchCustomers, path, setPath}) => {

    const onDrop = useCallback(acceptedFiles => {
        setPath(acceptedFiles.map(file => URL.createObjectURL(file)))

        const formData = new FormData();
        formData.append("file", acceptedFiles[0])
        // Do something with the files
        uploadCustomerProfilePicture(
            customerId, formData
        ).then(() => {
            sleep(2000).then(() => {
                fetchCustomers()
                successNotification("Success","Profile picture updated")
            });

        }).catch(() => {
            errorNotification("Error","Profile picture upload failed")
        })
        console.log(`localhost:8080/api/v1/customers/${customerId}/profile-image`);
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             borderRadius={'3xl'}
             p={6}
             rounded={'md'}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the picture here ...</p> :
                    <p>Drag 'n' drop picture here, or click to select picture</p>
            }

        </Box>
    )
}


// And now we can use these
const UpdateCustomerForm = ({ fetchCustomers, initialValues, customerId }) => {
    const [path, setPath] = useState([]);

    const profileImage =  Object.entries(path).length < 1 ? `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}/profile-image` : path;

    return (
        <>
            <VStack spacing={'5'} mb={'5'}>

                    <Image
                        borderRadius={'full'}
                        boxSize={'150px'}
                        objectFit={'cover'}
                        src={ profileImage }
                        key={profileImage}
                    />

               <MyDropzone customerId={customerId} fetchCustomers={fetchCustomers} path={path} setPath={setPath}/>
            </VStack>
            <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                })}
                onSubmit={(updatedCustomer, { setSubmitting }) => {
                    setSubmitting(true);
                    updateCustomer(customerId, updatedCustomer)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Customer updated",
                                `${updatedCustomer.name} was successfully updated`
                            )
                            fetchCustomers();
                        }).catch(err => {
                            console.log(err);
                            errorNotification(
                                err.code,
                                err.response.data.message
                            )
                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}
            >

                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="20"
                            />

                            <Button
                                isDisabled={ !(isValid && dirty) || isSubmitting }
                                mt={4}
                                type={"submit"}
                                colorScheme={"teal"}
                            >Submit</Button>
                        </Stack>
                    </Form>
                )}

            </Formik>
        </>
    );
};


export default UpdateCustomerForm;
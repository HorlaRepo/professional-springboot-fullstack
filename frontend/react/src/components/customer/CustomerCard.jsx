'use client'

import {
    Heading,
    Avatar,
    Box,
    Center,
    AlertDialog,
    AlertDialogBody,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogContent,
    AlertDialogOverlay,
    Image,
    Flex,
    Text,
    Stack,
    Button,
    IconButton,
    useColorModeValue, Tag, useDisclosure
} from '@chakra-ui/react'
 import {useRef} from "react";
import { DeleteIcon } from '@chakra-ui/icons'
import {deleteCustomer} from "../../services/client.js"
import {successNotification, errorNotification} from "../../services/Notification.js"
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers, profileImageId}) {
    const randomUserGender = gender === "MALE" ? "men" : "women";
    const randomImage = `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`;
    const profileImage = profileImageId === null ? randomImage : `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`;


    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = useRef()
    return (
        <Center py={6}>
            <Box
                maxW={'350px'}
                minW={'280px'}
                w={'320px'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'lg'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit="cover"
                    alt="#"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            profileImage
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={2}>
                    <Stack spacing={2} align={'center'} mb={4}>
                        <Tag borderRadius={"full"}>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                           {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age {age} | {gender}</Text>
                    </Stack>
                </Box>
                <Stack direction={'row'} spacing={2} align={'center'} justify={'center'} mb={8}>
                    <IconButton
                        align={'center'}
                        mt={4}
                        variant='outline'
                        colorScheme='red'
                        aria-label='Call Sage'
                        fontSize='15px'
                        icon={<DeleteIcon />}
                        onClick={onOpen}
                    />

                    <UpdateCustomerDrawer
                        initialValues={{ name, email, age }}
                        customerId={id}
                        fetchCustomers={fetchCustomers}
                    />


                    <AlertDialog
                        isOpen={isOpen}
                        leastDestructiveRef={cancelRef}
                        onClose={onClose}
                    >
                        <AlertDialogOverlay>
                            <AlertDialogContent>
                                <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                    Delete Customer
                                </AlertDialogHeader>

                                <AlertDialogBody>
                                    Delete {name}? You can't undo this action afterwards.
                                </AlertDialogBody>

                                <AlertDialogFooter>
                                    <Button ref={cancelRef} onClick={onClose}>
                                        Cancel
                                    </Button>
                                    <Button colorScheme='red' onClick={() => {
                                        deleteCustomer(id).then(res => {
                                            successNotification(
                                                'Customer deleted',
                                                `${name} was successfully deleted`
                                            )
                                            fetchCustomers();
                                        }).catch(err => {
                                            console.log(err);
                                            errorNotification(
                                                err.code,
                                                err.response.data.message
                                            )
                                        }).finally( () => {
                                            onClose()
                                        })
                                    }} ml={3}>
                                        Delete
                                    </Button>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialogOverlay>
                    </AlertDialog>
                </Stack>
            </Box>
        </Center>
    )
}
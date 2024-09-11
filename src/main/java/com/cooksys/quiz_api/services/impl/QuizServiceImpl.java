package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.AnswerMapper;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuizMapper quizMapper;
  private final QuestionRepository questionRepository;
  private final QuestionMapper questionMapper;
  private final AnswerRepository answerRepository;
  private final AnswerMapper answerMapper;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
//    Quiz quiz = new Quiz();
//    quiz.setName(quizRequestDto.getName());
//////    quiz.setQuestions(quizRequestDto.getQuestions());
//////    System.out.println(quiz.getQuestions());
//    return quizMapper.entityToDto(quizRepository.saveAndFlush(quiz));

//    return quizMapper.entityToResponseDto(quizRepository.saveAndFlush(quizMapper.requestDtoToEntity(quizRequestDto)));

    Quiz quizToSave = quizRepository.saveAndFlush(quizMapper.requestDtoToEntity(quizRequestDto));
//    quizToSave.setName(quizToSave.getName());
//    quizToSave.setQuestions(questionMapper.requestDtoToEntity(questionMapper.entityToDto(quizToSave.getQuestions().get(0))));
//    quizToSave.setQuestions(quizToSave.getQuestions());
//    return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave));

    for (Question q: quizToSave.getQuestions()) {
      q.setQuiz(quizToSave);
      questionRepository.saveAndFlush(q); //saves each question
      for (Answer a: q.getAnswers()) {
        a.setQuestion(q);
        answerRepository.saveAndFlush(a); //saves each question's answers
      }
    }
    return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave));
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {
    Quiz quizToDelete = getQuiz(id);
    quizToDelete.setDeleted(true);
    return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToDelete));
  }

  @Override
  public QuizResponseDto renameQuiz(Long id, String newName) {
    Quiz quizToRename = getQuiz(id);
//    System.out.println(quizToRename.getName());
    quizToRename.setName(newName);
    return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToRename));
  }

  @Override
  public QuestionResponseDto randomQuestion(Long id) {
    Quiz quiz = getQuiz(id);
    int randomNumber = new Random().nextInt(quiz.getQuestions().size());
    return questionMapper.entityToDto(quiz.getQuestions().get(randomNumber));
  }

  @Override
  public QuizResponseDto addQuestion(Long id, QuestionRequestDto questionRequestDto) {
    Quiz quiz = getQuiz(id);
//    quiz.getQuestions().add(questionMapper.requestDtoToEntity(questionRequestDto));
    Question question = questionMapper.requestDtoToEntity(questionRequestDto);

//    for (Answer a: question.getAnswers()) {
//      a.setQuestion(question);
//      answerRepository.saveAndFlush(a);
//    }
//    question.setQuiz(quiz);
//    questionRepository.saveAndFlush(question);
//
//    return quizMapper.entityToDto(quizRepository.saveAndFlush(quiz));

    question.setQuiz(quiz);
    question = questionRepository.saveAndFlush(question);
    for (Answer a : question.getAnswers()) {
      a.setQuestion(question);
      answerRepository.saveAndFlush(a); //saves each question's answers
    }
    quiz.getQuestions().add(question);  //add the new question

    return quizMapper.entityToDto(quizRepository.saveAndFlush(quiz));
  }

  @Override
  public QuestionResponseDto deleteQuestion(Long id, Long questionID) {
//    Quiz quiz = getQuiz(id);
    Question question = getQuestion(questionID);
    question.setDeleted(true);
    return questionMapper.entityToDto(questionRepository.saveAndFlush(question));
  }

  public Quiz getQuiz(Long id) {
    Optional<Quiz> optionalQuiz = quizRepository.findByIdAndDeletedFalse(id);
    return optionalQuiz.get();
  }

  public Question getQuestion(Long id) {
    Optional<Question> optionalQuestion = questionRepository.findByIdAndDeletedFalse(id);
    return optionalQuestion.get();
  }

}

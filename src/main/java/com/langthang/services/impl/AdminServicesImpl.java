package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.PostReportDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.dto.SystemReportDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.Post;
import com.langthang.model.PostReport;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PostReportRepository;
import com.langthang.repository.PostRepository;
import com.langthang.repository.impl.RoleRepository;
import com.langthang.services.IAdminServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class AdminServicesImpl implements IAdminServices {

    private final AccountRepository accRepo;

    private final RoleRepository roleRepo;

    private final PostRepository postRepo;

    private final PostReportRepository reportRepo;

    @Override
    public SystemReportDTO getBasicSystemReport() {
        long numberOfAccount = accRepo.count();

        long numberOfPost = postRepo.count();

        long numberOfReportedPost = reportRepo.count();

        return new SystemReportDTO(numberOfAccount, numberOfPost, numberOfReportedPost);
    }

    @Override
    public void assignRoleAdminToUser(int userId) {
        Account account = accRepo.findAccountByIdAndEnabled(userId, true);

        if (account == null) {
            throw new CustomException("No enabled account with id: " + userId,
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        account.setRole(roleRepo.findRoleByName("ROLE_ADMIN"));
        accRepo.saveAndFlush(account);
    }

    @Override
    public List<PostReportDTO> getListOfPostReport(Pageable pageable) {
        Page<PostReport> reportList = reportRepo.findAll(pageable);

        return reportList.map(this::toBasicPostReportDTO).getContent();
    }

    @Override
    public PostReportDTO getPostReportById(int reportId) {
        PostReport postReport = reportRepo.findById(reportId).orElse(null);
        if (postReport == null) {
            throw new CustomException("Report with ID: " + reportId + " not found", HttpStatus.NOT_FOUND);
        }

        return toPostReportDetailDTO(postReport);
    }

    @Override
    public PostReportDTO solveReport(int reportId, String decision) {
        PostReport report = reportRepo.findById(reportId).orElse(null);
        if (report == null) {
            throw new CustomException("Report with ID: " + reportId + " not found", HttpStatus.NOT_FOUND);
        }
        if (report.isSolved()) {
            throw new CustomException("Already solved", HttpStatus.NOT_ACCEPTABLE);
        }

        report.setDecision(decision);
        report.setSolved(true);

        PostReport savedReport = reportRepo.saveAndFlush(report);

        return toBasicPostReportDTO(savedReport);
    }

    private PostReportDTO toBasicPostReportDTO(PostReport postReport) {

        return PostReportDTO.builder()
                .reportId(postReport.getId())
                .reportDate(postReport.getReportedDate())
                .solved(postReport.isSolved())
                .reportContent(postReport.getContent())
                .decision(postReport.getDecision())
                .build();
    }

    private PostReportDTO toPostReportDetailDTO(PostReport postReport) {
        Account reporter = postReport.getAccount();
        AccountDTO reporterDTO = AccountDTO.toBasicAccount(reporter);

        Post reportedPost = postReport.getPost();
        PostResponseDTO reportedPostDTO = null;
        if (reportedPost != null) {
            reportedPostDTO = new PostResponseDTO(reportedPost.getId(), reportedPost.getSlug());
        }

        PostReportDTO postReportDTO = toBasicPostReportDTO(postReport);
        postReportDTO.setReporter(reporterDTO);
        postReportDTO.setReportedPost(reportedPostDTO);

        return postReportDTO;
    }
}

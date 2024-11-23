/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.renderdoc_jffm.tests;

import com.io7m.renderdoc_jffm.core.RenderDoc;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.APIValidation;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.AllowFullscreen;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.AllowVSync;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureAllCmdLists;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureCallstacks;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureCallstacksOnlyActions;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.DebugOutputMute;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.DelayForDebugger;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.HookIntoChildren;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.RefAllResources;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.SoftMemoryLimit;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.VerifyBufferAccess;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public final class RenderDocTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RenderDocTest.class);

  @Test
  public void testBasicFunctionality()
    throws IOException
  {
    System.out.println(ProcessHandle.current().pid());

    try (final var doc = RenderDoc.open()) {
      LOG.debug("{}", doc.option(APIValidation.class));
      LOG.debug("{}", doc.option(AllowFullscreen.class));
      LOG.debug("{}", doc.option(AllowVSync.class));
      LOG.debug("{}", doc.option(CaptureAllCmdLists.class));
      LOG.debug("{}", doc.option(CaptureCallstacks.class));
      LOG.debug("{}", doc.option(CaptureCallstacksOnlyActions.class));
      LOG.debug("{}", doc.option(DebugOutputMute.class));
      LOG.debug("{}", doc.option(DelayForDebugger.class));
      LOG.debug("{}", doc.option(HookIntoChildren.class));
      LOG.debug("{}", doc.option(RefAllResources.class));
      LOG.debug("{}", doc.option(SoftMemoryLimit.class));
      LOG.debug("{}", doc.option(VerifyBufferAccess.class));

      LOG.debug("{}", doc.captureFilePathTemplate());
      doc.setCaptureFilePathTemplate(
        Paths.get("captures")
          .toAbsolutePath()
      );
      LOG.debug("{}", doc.captureFilePathTemplate());

      LOG.debug("{}", doc.numberOfCaptures());
      doc.triggerCapture();
      LOG.debug("{}", doc.numberOfCaptures());
    }
  }
}
